import sys
import os
from pathlib import Path
import cv2
import numpy as np

ALLOWED_EXT = {'.jpg', '.jpeg', '.png'}

def auto_canny(image, sigma=0.33):
    v = np.median(image)
    lower = int(max(0, (1.0 - sigma) * v))
    upper = int(min(255, (1.0 + sigma) * v))
    return cv2.Canny(image, lower, upper)

def process_image(path_in, path_out):
    img = cv2.imread(str(path_in))
    if img is None:
        print(f"  [!] Не удалось открыть {path_in}")
        return False

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    blur = cv2.GaussianBlur(gray, (5,5), 0)

    edges = auto_canny(blur, sigma=0.33)

    kernel = np.ones((3,3), np.uint8)
    edges_dilated = cv2.dilate(edges, kernel, iterations=1)

    overlay = img.copy()
    overlay[edges_dilated > 0] = (0, 0, 255)

    alpha = 0.6
    blended = img.copy()
    mask = edges_dilated > 0
    blended[mask] = (blended[mask] * alpha + np.array([0,0,255]) * (1 - alpha)).astype(np.uint8)

    cv2.imwrite(str(path_out), blended)
    return True

def main():
    input_folder = Path('img')
    if not input_folder.exists() or not input_folder.is_dir():
        print(f"[ERROR] Папка не найдена или это не папка: {input_folder}")
        sys.exit(1)

    results_dir = input_folder.resolve().parent / "results"
    results_dir.mkdir(parents=True, exist_ok=True)
    print(f"Вход: {input_folder}")
    print(f"Результаты будут в: {results_dir}")

    files = sorted([p for p in input_folder.iterdir() if p.suffix.lower() in ALLOWED_EXT])
    if not files:
        print("Нет подходящих изображений в папке.")
        sys.exit(0)

    total = len(files)
    print(f"Найдено {total} изображений. Обработка...")

    count = 0
    for p in files:
        out_path = results_dir / p.name
        ok = process_image(p, out_path)
        if ok:
            count += 1
            print(f"  [{count}/{total}] {p.name} -> saved")
        else:
            print(f"  [!] Ошибка при обработке {p.name}")

    print(f"Готово: обработано {count}/{total} изображений. Результаты в {results_dir}")

if __name__ == "__main__":
    main()
