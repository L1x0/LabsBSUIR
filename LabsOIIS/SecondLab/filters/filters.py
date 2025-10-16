import cv2
import numpy as np
import sys
import os

def apply_median(img, k=3):
    return cv2.medianBlur(img, k)

def apply_gaussian(img, k=5, sigma=1.0):
    return cv2.GaussianBlur(img, (k, k), sigma)

def apply_box(img, k=3):
    return cv2.blur(img, (k, k))

def apply_sharpen(img):
    kernel = np.array([[0, -1, 0],
                       [-1, 5, -1],
                       [0, -1, 0]], dtype=np.float32)
    return cv2.filter2D(img, -1, kernel)

def apply_sobel_edge(img):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    sx = cv2.Sobel(gray, cv2.CV_64F, 1, 0, ksize=3)
    sy = cv2.Sobel(gray, cv2.CV_64F, 0, 1, ksize=3)
    mag = np.sqrt(sx* sx + sy* sy)
    mag = np.clip(mag / mag.max() * 255.0, 0, 255).astype(np.uint8)
    return cv2.cvtColor(mag, cv2.COLOR_GRAY2BGR)

def main(input_path, out_dir):
    img = cv2.imread(input_path)
    if img is None:
        print("Не удалось загрузить изображение:", input_path); return

    os.makedirs(out_dir, exist_ok=True)
    base = os.path.splitext(os.path.basename(input_path))[0]

    cv2.imwrite(os.path.join(out_dir, f"{base}_median.jpg"), apply_median(img, k=5))
    cv2.imwrite(os.path.join(out_dir, f"{base}_gaussian.jpg"), apply_gaussian(img, k=7, sigma=1.5))
    cv2.imwrite(os.path.join(out_dir, f"{base}_box.jpg"), apply_box(img, k=5))
    cv2.imwrite(os.path.join(out_dir, f"{base}_sharpen.jpg"), apply_sharpen(img))
    cv2.imwrite(os.path.join(out_dir, f"{base}_sobel.jpg"), apply_sobel_edge(img))

    print("Готово. Результаты в:", out_dir)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Использование: python filters.py input.jpg out_dir")
    else:
        main(sys.argv[1], sys.argv[2])