import cv2
import numpy as np
from pathlib import Path

try:
    import tensorflow as tf
    from tensorflow import keras
    from tensorflow.keras import layers
    HAS_TENSORFLOW = True
except ImportError:
    HAS_TENSORFLOW = False

def create_cnn_classifier():
    if not HAS_TENSORFLOW:
        return None
    model = keras.Sequential([
        layers.Conv2D(32, (3, 3), activation='relu', input_shape=(64, 64, 3)),
        layers.MaxPooling2D((2, 2)),
        layers.Conv2D(64, (3, 3), activation='relu'),
        layers.MaxPooling2D((2, 2)),
        layers.Conv2D(64, (3, 3), activation='relu'),
        layers.Flatten(),
        layers.Dense(64, activation='relu'),
        layers.Dropout(0.5),
        layers.Dense(1, activation='sigmoid')
    ])
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
    return model

def classify_with_cnn(region, model):
    if model is None or not HAS_TENSORFLOW:
        return None
    resized = cv2.resize(region, (64, 64))
    if len(resized.shape) == 2:
        resized = cv2.cvtColor(resized, cv2.COLOR_GRAY2BGR)
    img_array = np.expand_dims(resized / 255.0, axis=0)
    prediction = model.predict(img_array, verbose=0)[0][0]
    return prediction

def is_black_white_ball(region, cnn_model=None):
    if len(region.shape) == 3:
        gray = cv2.cvtColor(region, cv2.COLOR_BGR2GRAY)
        hsv = cv2.cvtColor(region, cv2.COLOR_BGR2HSV)
        saturation = hsv[:, :, 1]
        avg_saturation = np.mean(saturation)
    else:
        gray = region
        avg_saturation = 0
    if gray.size == 0:
        return False
    region_size = gray.size
    size_factor = min(1.0, region_size / 2000.0)
    if avg_saturation > 75:
        return False
    std_dev = np.std(gray)
    mean_val = np.mean(gray)
    black_ratio = np.sum(gray < 75) / gray.size
    white_ratio = np.sum(gray > 175) / gray.size
    min_ratio_thresh = max(0.05, 0.07 - 0.02 * size_factor)
    if black_ratio < min_ratio_thresh or white_ratio < min_ratio_thresh:
        return False
    min_std = max(22, 25 - int(3 * size_factor))
    if std_dev < min_std:
        return False
    if max(black_ratio, white_ratio) > 0:
        color_balance = min(black_ratio, white_ratio) / max(black_ratio, white_ratio)
        if color_balance < 0.25:
            return False
    if cnn_model is not None:
        cnn_score = classify_with_cnn(region, cnn_model)
        if cnn_score is not None:
            if cnn_score < 0.3:
                return False
            if cnn_score > 0.7:
                return True
    return True

def draw_cube(image, center, size):
    x, y = center
    side = int(size * 0.7)
    offset = int(side * 0.866)
    front_face = np.array([
        [x - side//2, y - side//2],
        [x + side//2, y - side//2],
        [x + side//2, y + side//2],
        [x - side//2, y + side//2],
    ], np.int32)
    back_face = np.array([
        [x - side//2 - offset//2, y - side//2 - offset//2],
        [x + side//2 - offset//2, y - side//2 - offset//2],
        [x + side//2 - offset//2, y + side//2 - offset//2],
        [x - side//2 - offset//2, y + side//2 - offset//2],
    ], np.int32)
    left_face = np.array([back_face[0], back_face[3], front_face[3], front_face[0]], np.int32)
    right_face = np.array([back_face[1], back_face[2], front_face[2], front_face[1]], np.int32)
    cv2.fillPoly(image, [back_face], (180, 180, 180))
    cv2.polylines(image, [back_face], True, (100, 100, 100), 2)
    cv2.fillPoly(image, [left_face], (200, 200, 200))
    cv2.polylines(image, [left_face], True, (100, 100, 100), 2)
    cv2.fillPoly(image, [right_face], (220, 220, 220))
    cv2.polylines(image, [right_face], True, (100, 100, 100), 2)
    cv2.fillPoly(image, [front_face], (240, 240, 240))
    cv2.polylines(image, [front_face], True, (50, 50, 50), 2)
    for i in range(4):
        cv2.line(image, tuple(back_face[i]), tuple(front_face[i]), (50, 50, 50), 2)

def remove_duplicates(balls, image_size=None):
    if not balls:
        return []
    sorted_balls = sorted(balls, key=lambda b: b[2], reverse=True)
    unique_balls = []
    aggressive_mode = False
    if len(balls) > 2 and image_size:
        min_distances = []
        max_radius = max(r for _, _, r in balls)
        for i, (x1, y1, r1) in enumerate(balls):
            min_dist = float('inf')
            for j, (x2, y2, r2) in enumerate(balls):
                if i != j:
                    dist = np.sqrt((x1 - x2)**2 + (y1 - y2)**2)
                    avg_r = (r1 + r2) / 2
                    normalized_dist = dist / avg_r if avg_r > 0 else float('inf')
                    min_dist = min(min_dist, normalized_dist)
            if min_dist != float('inf'):
                min_distances.append(min_dist)
        if min_distances:
            avg_min_dist = sum(min_distances) / len(min_distances)
            image_diagonal = np.sqrt(image_size[0]**2 + image_size[1]**2)
            size_ratio = max_radius / image_diagonal
            aggressive_mode = (avg_min_dist < 2.0) or (size_ratio > 0.2 and len(balls) > 3 and avg_min_dist < 4.0)
    for (x, y, r) in sorted_balls:
        is_duplicate = False
        best_match_idx = -1
        best_match_radius = -1
        for idx, (ux, uy, ur) in enumerate(unique_balls):
            dist = np.sqrt((x - ux)**2 + (y - uy)**2)
            min_radius = min(r, ur)
            max_radius = max(r, ur)
            avg_radius = (r + ur) / 2
            sum_radius = r + ur
            overlap_threshold = avg_radius * (1.2 if aggressive_mode else 1.6)
            inside_threshold = 0.6 if aggressive_mode else 0.75
            intersection_threshold = sum_radius * (0.6 if aggressive_mode else 0.8)
            if dist < overlap_threshold:
                if r > best_match_radius:
                    best_match_idx = idx
                    best_match_radius = r
                is_duplicate = True
            elif dist + min_radius < max_radius * inside_threshold:
                if r > best_match_radius:
                    best_match_idx = idx
                    best_match_radius = r
                is_duplicate = True
            elif dist < intersection_threshold:
                if r > best_match_radius:
                    best_match_idx = idx
                    best_match_radius = r
                is_duplicate = True
        if is_duplicate and best_match_idx >= 0:
            if r > unique_balls[best_match_idx][2]:
                unique_balls[best_match_idx] = (x, y, r)
        elif not is_duplicate:
            unique_balls.append((x, y, r))
    if aggressive_mode and len(unique_balls) > 1:
        unique_balls = sorted(unique_balls, key=lambda b: b[2], reverse=True)
        max_radius = unique_balls[0][2]
        if image_size:
            image_diagonal = np.sqrt(image_size[0]**2 + image_size[1]**2)
            if max_radius / image_diagonal > 0.15:
                return [unique_balls[0]]
        final_balls = [unique_balls[0]]
        for (x, y, r) in unique_balls[1:]:
            too_close = False
            for (ux, uy, ur) in final_balls:
                dist = np.sqrt((x - ux)**2 + (y - uy)**2)
                if dist < max(max_radius, ur) * 2.5:
                    too_close = True
                    break
            if not too_close:
                final_balls.append((x, y, r))
        return final_balls
    return unique_balls

def detect_football_balls(image, cnn_model=None):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    candidates = []
    max_dim = max(image.shape[0], image.shape[1])
    min_dim = min(image.shape[0], image.shape[1])
    blur_sizes = [(5, 5), (9, 9), (13, 13)]
    for blur_size in blur_sizes:
        blurred = cv2.GaussianBlur(gray, blur_size, 2)
        param_sets = [
            (1, 10, 50, 18, 3, min_dim // 10),
            (1, 12, 45, 20, 5, min_dim // 8),
            (1, 15, 50, 20, 8, min_dim // 6),
            (1, 18, 45, 22, 10, min_dim // 5),
            (1, 20, 50, 22, 12, min_dim // 4),
            (1, 25, 45, 25, 15, min_dim // 3),
            (1, 30, 50, 25, 20, min_dim // 2),
            (1, 35, 45, 28, 25, max_dim // 3),
            (1, 40, 50, 30, 30, max_dim // 2),
        ]
        for dp, minDist, param1, param2, minR, maxR in param_sets:
            if minR >= maxR or maxR < 5:
                continue
            circles = cv2.HoughCircles(
                blurred,
                cv2.HOUGH_GRADIENT,
                dp=dp,
                minDist=minDist,
                param1=param1,
                param2=param2,
                minRadius=minR,
                maxRadius=maxR
            )
            if circles is not None:
                circles = np.round(circles[0, :]).astype("int")
                for (x, y, r) in circles:
                    if x < 0 or y < 0 or x >= image.shape[1] or y >= image.shape[0] or r < 3:
                        continue
                    check_radius = int(r * 1.4)
                    y1, y2 = max(0, y - check_radius), min(image.shape[0], y + check_radius)
                    x1, x2 = max(0, x - check_radius), min(image.shape[1], x + check_radius)
                    region = image[y1:y2, x1:x2]
                    if region.size == 0 or region.shape[0] < 10 or region.shape[1] < 10:
                        continue
                    if is_black_white_ball(region, cnn_model=cnn_model):
                        candidates.append((x, y, r))
    for low_thresh in [20, 30, 40, 50, 60]:
        edges = cv2.Canny(gray, low_thresh, low_thresh * 2)
        circles = cv2.HoughCircles(
            edges,
            cv2.HOUGH_GRADIENT,
            dp=1,
            minDist=15,
            param1=50,
            param2=18,
            minRadius=5,
            maxRadius=max_dim // 2
        )
        if circles is not None:
            circles = np.round(circles[0, :]).astype("int")
            for (x, y, r) in circles:
                if x < 0 or y < 0 or x >= image.shape[1] or y >= image.shape[0] or r < 5:
                    continue
                check_radius = int(r * 1.4)
                y1, y2 = max(0, y - check_radius), min(image.shape[0], y + check_radius)
                x1, x2 = max(0, x - check_radius), min(image.shape[1], x + check_radius)
                region = image[y1:y2, x1:x2]
                if region.size > 0 and region.shape[0] >= 10 and region.shape[1] >= 10:
                    if is_black_white_ball(region, cnn_model=cnn_model):
                        candidates.append((x, y, r))
    for block_size in [9, 11, 13, 15, 17, 19]:
        adaptive_thresh = cv2.adaptiveThreshold(
            gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, block_size, 2
        )
        contours, _ = cv2.findContours(adaptive_thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        for contour in contours:
            area = cv2.contourArea(contour)
            if area < 50 or area > 100000:
                continue
            (x, y), radius = cv2.minEnclosingCircle(contour)
            x, y, radius = int(x), int(y), int(radius)
            if radius < 5 or radius > max_dim // 2:
                continue
            if cv2.arcLength(contour, True) > 0:
                circularity = 4 * np.pi * area / (cv2.arcLength(contour, True) ** 2)
                if circularity < 0.55:
                    continue
            check_radius = int(radius * 1.4)
            y1, y2 = max(0, y - check_radius), min(image.shape[0], y + check_radius)
            x1, x2 = max(0, x - check_radius), min(image.shape[1], x + check_radius)
            region = image[y1:y2, x1:x2]
            if region.size > 0 and region.shape[0] >= 10 and region.shape[1] >= 10:
                if is_black_white_ball(region, cnn_model=cnn_model):
                    candidates.append((x, y, radius))
    return remove_duplicates(candidates, image_size=(image.shape[0], image.shape[1]))

def process_image(image_path, output_dir, cnn_model=None):
    print(f"Обработка: {image_path.name}")
    image = cv2.imread(str(image_path))
    if image is None:
        print(f"Ошибка: не удалось загрузить {image_path}")
        return
    balls = detect_football_balls(image, cnn_model=cnn_model)
    print(f"Найдено мячей: {len(balls)}")
    for (x, y, r) in balls:
        cv2.circle(image, (x, y), r + 2, (255, 255, 255), -1)
        draw_cube(image, (x, y), r)
    output_path = output_dir / image_path.name
    cv2.imwrite(str(output_path), image)
    print(f"Сохранено: {output_path.name}")

def main():
    base_dir = Path(__file__).parent
    img_dir = base_dir / "img"
    results_dir = base_dir / "results"
    results_dir.mkdir(exist_ok=True)
    if not img_dir.exists():
        print(f"Ошибка: папка {img_dir} не найдена")
        return
    image_extensions = {'.jpg', '.jpeg', '.png', '.bmp', '.tiff', '.tif'}
    image_files = [
        f for f in img_dir.iterdir()
        if f.is_file() and f.suffix.lower() in image_extensions
    ]
    if not image_files:
        print(f"Ошибка: в папке {img_dir} не найдено изображений")
        return
    print(f"Найдено изображений: {len(image_files)}")
    cnn_model = None
    if HAS_TENSORFLOW:
        print("Инициализация CNN...")
        cnn_model = create_cnn_classifier()
        print("CNN готова")
    for image_file in image_files:
        process_image(image_file, results_dir, cnn_model=cnn_model)
    print("\nГотово!")

if __name__ == "__main__":
    main()
