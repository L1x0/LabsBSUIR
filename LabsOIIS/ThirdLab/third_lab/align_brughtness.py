from pathlib import Path
import cv2
import numpy as np
from skimage.exposure import match_histograms
import inspect
import sys


def load_image(path: Path):
    img = cv2.imdecode(np.fromfile(str(path), dtype=np.uint8), cv2.IMREAD_UNCHANGED)
    if img is None:
        raise RuntimeError(f"Can't read image: {path}")
    if img.ndim == 3 and img.shape[2] == 4:
        img = cv2.cvtColor(img, cv2.COLOR_BGRA2BGR)
    return img


def save_image(path: Path, img: np.ndarray):
    ext = path.suffix
    success, encoded = cv2.imencode(ext, img)
    if not success:
        raise RuntimeError(f"Failed to encode image for saving: {path}")
    encoded.tofile(str(path))


def is_color(img):
    return img is not None and img.ndim == 3 and img.shape[2] == 3


def compute_l_channel(img: np.ndarray) -> np.ndarray:
    if not is_color(img):
        return img.astype(np.float32)
    lab = cv2.cvtColor(img, cv2.COLOR_BGR2LAB)
    return lab[..., 0].astype(np.float32)



def safe_match_histograms(source: np.ndarray, template: np.ndarray) -> np.ndarray:
    try:
        sig = inspect.signature(match_histograms)
        if 'channel_axis' in sig.parameters:
            return match_histograms(source, template, channel_axis=None)
        elif 'multichannel' in sig.parameters:
            return match_histograms(source, template, multichannel=False)
    except Exception:
        pass

    src = source.ravel().astype(np.float64)
    tmpl = template.ravel().astype(np.float64)

    src_values, src_inverse, src_counts = np.unique(src, return_inverse=True, return_counts=True)
    tmpl_values, tmpl_counts = np.unique(tmpl, return_counts=True)

    src_cdf = np.cumsum(src_counts).astype(np.float64)
    src_cdf /= src_cdf[-1]

    tmpl_cdf = np.cumsum(tmpl_counts).astype(np.float64)
    tmpl_cdf /= tmpl_cdf[-1]
    interp_t_values = np.interp(src_cdf, tmpl_cdf, tmpl_values)
    matched = interp_t_values[src_inverse].reshape(source.shape)

    matched = np.clip(matched, 0, 255)
    return matched.astype(np.uint8)


def match_mean_std_lab(target: np.ndarray, ref_mean: float, ref_std: float) -> np.ndarray:
    if is_color(target):
        lab = cv2.cvtColor(target, cv2.COLOR_BGR2LAB).astype(np.float32)
        L = lab[..., 0]

        mean_t = float(L.mean())
        std_t = float(L.std())

        scale = 1.0 if std_t <= 1e-6 else (ref_std / std_t)
        L_adj = (L - mean_t) * scale + ref_mean
        lab[..., 0] = np.clip(L_adj, 0, 255)

        return cv2.cvtColor(lab.astype(np.uint8), cv2.COLOR_LAB2BGR)
    else:
        L = target.astype(np.float32)

        mean_t = float(L.mean())
        std_t = float(L.std())

        scale = 1.0 if std_t <= 1e-6 else (ref_std / std_t)
        L_adj = (L - mean_t) * scale + ref_mean

        return np.clip(L_adj, 0, 255).astype(np.uint8)


def match_hist_luminance(target: np.ndarray, reference: np.ndarray) -> np.ndarray:

    if is_color(target):
        tgt_lab = cv2.cvtColor(target, cv2.COLOR_BGR2LAB)
        ref_lab = cv2.cvtColor(reference, cv2.COLOR_BGR2LAB)

        L_t = tgt_lab[..., 0]
        L_r = ref_lab[..., 0]

        matched_L = safe_match_histograms(L_t, L_r)
        tgt_lab[..., 0] = np.clip(matched_L, 0, 255).astype(np.uint8)

        return cv2.cvtColor(tgt_lab, cv2.COLOR_LAB2BGR)
    else:
        matched = safe_match_histograms(target, reference)
        return np.clip(matched, 0, 255).astype(np.uint8)


def main():
    in_dir = Path('images')
    out_dir = Path('results')

    if not in_dir.exists() or not in_dir.is_dir():
        print("Папка 'images' не найдена в текущей директории. Создайте папку и поместите туда изображения.")
        sys.exit(1)

    out_dir.mkdir(parents=True, exist_ok=True)

    allowed = {'.jpg', '.jpeg', '.png', '.tif', '.tiff', '.bmp'}
    image_paths = sorted([p for p in in_dir.iterdir() if p.is_file() and p.suffix.lower() in allowed])

    if not image_paths:
        print("В папке 'images' не найдено изображений.")
        sys.exit(1)

    print(f"Найдено {len(image_paths)} файлов. Введите метод:")
    print(" 1 — mean_std (подгонка L-канала по медианным mean/std)")
    print(" 2 — hist (подгонка гистограммы L-канала к медианному изображению)")

    choice = input("Выбор (1 или 2): ").strip()

    if choice not in ('1', '2'):
        print("Неверный выбор. Выход.")
        sys.exit(1)

    if choice == '1':
        means = []
        stds = []

        for p in image_paths:
            img = load_image(p)
            L = compute_l_channel(img)
            means.append(float(L.mean()))
            stds.append(float(L.std()))

        ref_mean = float(np.median(means))
        ref_std = float(np.median(stds))
        print(f"Опорные значения: mean={ref_mean:.2f}, std={ref_std:.2f}")

        for p in image_paths:
            try:
                img = load_image(p)
                out = match_mean_std_lab(img, ref_mean, ref_std)
                out_path = out_dir / p.name
                save_image(out_path, out)
                print(f"Сохранено: {out_path}")
            except Exception as e:
                print(f"Ошибка для {p.name}: {e}")

    else:
        areas = [p.stat().st_size for p in image_paths]
        median_idx = int(np.argsort(areas)[len(areas) // 2])
        ref_path = image_paths[median_idx]
        reference = load_image(ref_path)
        print(f"Используется в качестве опоры: {ref_path.name}")

        for p in image_paths:
            try:
                img = load_image(p)
                out = match_hist_luminance(img, reference)
                out_path = out_dir / p.name
                save_image(out_path, out)
                print(f"Сохранено: {out_path}")
            except Exception as e:
                print(f"Ошибка для {p.name}: {e}")

    print("Готово. Результаты в папке 'results'.")


if __name__ == '__main__':
    main()
