from pathlib import Path
import cv2
import numpy as np
import sys

IMG_DIR = Path("img")
ALLOWED_EXT = {".jpg", ".jpeg", ".png", ".bmp", ".tif", ".tiff", ".webp"}

def find_two_images(folder: Path):
    if not folder.exists() or not folder.is_dir():
        raise FileNotFoundError(f"Папка не найдена: {folder.resolve()}")
    left = None
    right = None
    for name in ("left","LEFT","Left"):
        for ext in ALLOWED_EXT:
            p = folder / f"{name}{ext}"
            if p.exists():
                left = p
                break
        if left: break
    for name in ("right","RIGHT","Right"):
        for ext in ALLOWED_EXT:
            p = folder / f"{name}{ext}"
            if p.exists():
                right = p
                break
        if right: break

    files = sorted([p for p in folder.iterdir() if p.suffix.lower() in ALLOWED_EXT])
    if left and right:
        return left, right
    if len(files) >= 2:
        return files[0], files[1]
    raise FileNotFoundError(f"Нужно как минимум 2 изображения в {folder} (поддерживаемые расширения: {ALLOWED_EXT})")

def read_and_match_size(p_left: Path, p_right: Path):
    L = cv2.imread(str(p_left), cv2.IMREAD_COLOR)
    R = cv2.imread(str(p_right), cv2.IMREAD_COLOR)
    if L is None or R is None:
        raise IOError("Не удалось прочитать одно из изображений.")
    h, w = L.shape[:2]
    if R.shape[0:2] != (h, w):
        R = cv2.resize(R, (w, h), interpolation=cv2.INTER_AREA)
    return L, R

def shift_image_horiz(img, shift):
    h, w = img.shape[:2]
    out = np.zeros_like(img)
    if shift == 0:
        return img.copy()
    if shift > 0:
        out[:, shift:w] = img[:, 0:w-shift]
    else:
        s = -shift
        out[:, 0:w-s] = img[:, s:w]
    return out

def make_anaglyph(left, right, shift=0):
    Rshift = shift_image_horiz(right, shift)
    B_right = Rshift[:, :, 0]
    G_right = Rshift[:, :, 1]
    R_left = left[:, :, 2]
    anag = cv2.merge((B_right, G_right, R_left))
    return anag

def make_side_by_side(left, right):
    return np.hstack((left, right))

def main():
    try:
        p_left, p_right = find_two_images(IMG_DIR)
    except Exception as e:
        print("Ошибка:", e)
        sys.exit(1)

    left, right = read_and_match_size(p_left, p_right)
    print("Использую:")
    print("  left :", p_left.name)
    print("  right:", p_right.name)

    results_dir = IMG_DIR.resolve().parent / "results"
    results_dir.mkdir(parents=True, exist_ok=True)

    win_name = "Anaglyph (use trackbar to change shift, s=save, q/Esc=quit)"
    cv2.namedWindow(win_name, cv2.WINDOW_NORMAL)
    cv2.namedWindow("Side-by-Side", cv2.WINDOW_NORMAL)

    def nothing(x): pass
    cv2.createTrackbar("shift", win_name, 100, 200, nothing)

    last_pos = -999
    saved = False
    while True:
        pos = cv2.getTrackbarPos("shift", win_name)
        shift = pos - 100
        if pos != last_pos:
            anag = make_anaglyph(left, right, shift=shift)
            side = make_side_by_side(left, right)
            cv2.imshow(win_name, anag)
            cv2.imshow("Side-by-Side", side)
            last_pos = pos

        key = cv2.waitKey(50) & 0xFF
        if key == ord('q') or key == 27:
            break
        if key == ord('s'):
            anag_name = f"anaglyph_{p_left.stem}_{p_right.stem}_shift{shift}.png"
            side_name = f"sidebyside_{p_left.stem}_{p_right.stem}.png"
            cv2.imwrite(str(results_dir / anag_name), anag)
            cv2.imwrite(str(results_dir / side_name), side)
            print(f"Сохранено: {results_dir / anag_name}")
            print(f"Сохранено: {results_dir / side_name}")
            saved = True

    cv2.destroyAllWindows()
    if saved:
        print("Готово. Результаты в:", results_dir)
    else:
        print("Выход. (Если хотите сохранить результат — нажмите 's' перед выходом.)")

if __name__ == "__main__":
    main()
