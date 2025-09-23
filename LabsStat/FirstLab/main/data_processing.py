import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
import pandas as pd

plt.style.use('seaborn-v0_8')
sns.set_palette("husl")
plt.rcParams['figure.figsize'] = (10, 6)
plt.rcParams['font.size'] = 12

dataset = pd.read_csv("crocodile_dataset.csv")


def get_main_info():
    print(f"Размерность датасета: {dataset.shape}")
    print(f"\nТипы данных \n{dataset.dtypes}")
    print(f"\nПервая строка \n\n{dataset.iloc[0]},\n\n Последняя строка \n\n {dataset.iloc[len(dataset) - 1]}\n\n")

    print(f"{dataset.describe()}")
    print(f"\n\n{dataset.info()}")


def process_null_val():
    null_val_count = dataset.isnull().sum()

    print("Количество пропусков")
    print(null_val_count)

    if len(null_val_count) > 0:
        return dataset.dropna()
    else:
        return dataset


def process_duplicate_rows():
    duplicate_row_count = dataset.duplicated().sum()

    print(f"Количество дубликатов: {duplicate_row_count}")

    if duplicate_row_count > 0:
        return dataset.drop_duplicates()
    else:
        return dataset


def exhaust_process():
    Q1 = dataset['Observed Length (m)'].quantile(0.25)
    Q3 = dataset['Observed Length (m)'].quantile(0.75)
    IQR = Q3 - Q1
    lower_bound = Q1 - 1.5 * IQR
    upper_bound = Q3 + 1.5 * IQR

    df_filtered = dataset[
        (dataset['Observed Length (m)'] >= lower_bound) & (dataset['Observed Length (m)'] <= upper_bound)]

    Q1 = df_filtered['Observed Weight (kg)'].quantile(0.25)
    Q3 = df_filtered['Observed Weight (kg)'].quantile(0.75)
    IQR = Q3 - Q1
    lower_bound = Q1 - 1.5 * IQR
    upper_bound = Q3 + 1.5 * IQR

    return df_filtered[
        (df_filtered['Observed Weight (kg)'] >= lower_bound) & (df_filtered['Observed Weight (kg)'] <= upper_bound)]


def change_columns_type():
    dataset['Observation ID'] = dataset['Observation ID'].astype('int')
    dataset['Common Name'] = dataset['Common Name'].astype('string')
    dataset['Scientific Name'] = dataset['Scientific Name'].astype('string')
    dataset['Family'] = dataset['Family'].astype('category')
    dataset['Genus'] = dataset['Genus'].astype('category')
    dataset['Observed Length (m)'] = dataset['Observed Length (m)'].astype('float')
    dataset['Observed Weight (kg)'] = dataset['Observed Weight (kg)'].astype('float')
    dataset['Age Class'] = dataset['Age Class'].astype('category')
    dataset['Sex'] = dataset['Sex'].astype('string')
    dataset['Date of Observation'] = pd.to_datetime(dataset['Date of Observation'], dayfirst=True)
    dataset['Country/Region'] = dataset['Country/Region'].astype('category')
    dataset['Habitat Type'] = dataset['Habitat Type'].astype('category')
    dataset['Conservation Status'] = dataset['Conservation Status'].astype('category')
    dataset['Observer Name'] = dataset['Observer Name'].astype('string')
    dataset['Notes'] = dataset['Notes'].astype('string')

    return pd.get_dummies(dataset, columns=['Conservation Status'])


def common_name_relation():
    counts_common = dataset['Common Name'].value_counts()
    rel_common = dataset['Common Name'].value_counts(normalize=True) * 100

    print("Топ по common name:\n", counts_common.head(20))
    print("\nДоли (в %) по common name:\n", rel_common.head(20))


def length_and_weight_distribution():
    length_stats = dataset['Observed Length (m)'].describe(percentiles=[.01, .05, .25, .5, .75, .95, .99])
    weight_stats = dataset['Observed Weight (kg)'].describe(percentiles=[.01, .05, .25, .5, .75, .95, .99])

    print("Length (m) stats:\n", length_stats)
    print("\nWeight (kg) stats:\n", weight_stats)


def length_and_weight_relation():
    pearson_corr = dataset['Observed Length (m)'].corr(dataset['Observed Weight (kg)'], method='pearson')
    spearman_corr = dataset['Observed Length (m)'].corr(dataset['Observed Weight (kg)'], method='spearman')

    print(f"Корреляция по Пирсону: {pearson_corr}")
    print(f"Корреляция по Спирмену: {spearman_corr}")

    relation = abs((pearson_corr + spearman_corr) / 2)

    if relation < 0.25:
        print("Связь между длиной и весом слабая")
    elif 0.25 < relation < 0.75:
        print("Связь между длиной и весом умеренная")
    elif relation > 0.75:
        print("Связь между длиной и весом сильная")


def find_biggest_crocodiles():
    by_country = (dataset
                  .groupby('Country/Region', observed=True)['Observed Length (m)']
                  .agg(count='count', mean='mean', median='median', max='max')
                  .sort_values('mean', ascending=False))
    print(by_country.head(20))


def find_lightest_crocodiles():
    by_country = (dataset
                  .groupby('Country/Region', observed=True)['Observed Weight (kg)']
                  .agg(count='count', mean='mean', median='median', min="min")
                  .sort_values('mean', ascending=True))
    print(by_country.head(20))


def find_crocodile_friendly_place():
    by_country = (dataset
                  .groupby('Country/Region', observed=True)['Common Name']
                  .agg(count='count')
                  .sort_values('count', ascending=False))
    print(by_country.head(20))


def avg_crocodile_length_per_year():
    yearly = dataset.resample('YE')['Observed Length (m)'].mean()

    print(yearly)


def avg_crocodile_weight_per_month():
    monthly = dataset.resample('ME')['Observed Weight (kg)'].mean()

    print(monthly)


def first_observed_crocodile():
    print(dataset.loc[dataset.index.min()])


def crocodile_sex_analysis():
    cols = ['Sex', 'Observed Length (m)', 'Observed Weight (kg)']
    df_sex = dataset[cols].dropna(subset=['Sex']).copy()

    result = (df_sex
              .groupby('Sex', observed=True)
              .agg(
        avg_length_m=('Observed Length (m)', 'mean'),
        avg_weight_kg=('Observed Weight (kg)', 'mean'),
        count=('Observed Length (m)', 'count')
    ).round(3).sort_values('avg_length_m', ascending=False))

    print(result)


get_main_info()
dataset = process_null_val()
dataset = process_duplicate_rows()
#dataset = exhaust_process()

print(f"Размерность датасета после фильтрации {dataset.shape}")

dataset = change_columns_type()

# Какие виды (Common Name) встречаются в датасете и как часто каждый из них наблюдался?
common_name_relation()

# Как распределяются длина и масса крокодилов?
length_and_weight_distribution()

# Есть ли линейная связь между длиной и массой крокодилов?
length_and_weight_relation()

# Где самые большие крокодилы?
find_biggest_crocodiles()

# Где самые лёгкие крокодилы?
find_lightest_crocodiles()

# Где крокодилов больше всего?
find_crocodile_friendly_place()

dataset.set_index('Date of Observation', inplace=True)

# Средняя ежегодная длина
avg_crocodile_length_per_year()

# Средний ежемесячный вес
avg_crocodile_weight_per_month()

# первый записанный крокодил
first_observed_crocodile()

# отличие средних рост/вес по полу
crocodile_sex_analysis()


def make_length_histogram():
    plt.figure(figsize=(10, 6))
    plt.hist(dataset['Observed Length (m)'], bins=30, alpha=0.7, edgecolor='black')
    plt.title('Распределение длины')
    plt.xlabel('Длина крокодила')
    plt.ylabel('Частота')
    plt.grid(True, alpha=0.3)
    plt.show()


def make_weight_histogram():
    plt.figure(figsize=(10, 6))
    plt.hist(dataset['Observed Weight (kg)'], bins=30, alpha=0.7, edgecolor='black')
    plt.title('Распределение веса')
    plt.xlabel('Вес крокодила')
    plt.ylabel('Частота')
    plt.grid(True, alpha=0.3)
    plt.show()

def make_length_boxplot():
    plt.figure(figsize=(8, 6))
    plt.boxplot(dataset['Observed Length (m)'])
    plt.title('Коробчатая диаграмма роста')
    plt.ylabel('Рост')
    plt.show()


def make_weight_boxplot():
    plt.figure(figsize=(8, 6))
    plt.boxplot(dataset['Observed Weight (kg)'], showfliers=False)
    plt.title('Коробчатая диаграмма веса')
    plt.ylabel('Вес')
    plt.show()


def make_sex_barplot():
    plt.figure(figsize=(10, 6))
    dataset['Sex'].value_counts().plot(kind='bar')
    plt.title('Распределение по полу')
    plt.xlabel('Категория')
    plt.ylabel('Количество')
    plt.xticks(rotation=45)
    plt.show()


def make_region_barplot():
    plt.figure(figsize=(28, 9))
    dataset['Country/Region'].value_counts().plot(kind='bar')
    plt.title('Распределение по полу')
    plt.xlabel('Категория')
    plt.ylabel('Количество')
    plt.xticks(rotation=45)
    plt.show()


def corr_matrix():
    corr_matrix = dataset.select_dtypes(include=[np.number]).corr()
    plt.figure(figsize=(12, 8))
    sns.heatmap(corr_matrix, annot=True, cmap='coolwarm', center=0,
                square=True, fmt='.2f', cbar_kws={'label': 'Коэффициен корреляции'})
    plt.title('Матрица корреляций')
    plt.show()

def make_length_weight_scatter_plot():
    plt.figure(figsize=(10, 6))
    plt.scatter(dataset['Observed Length (m)'], dataset['Observed Weight (kg)'], alpha=0.6)
    plt.xlabel('Длина')
    plt.ylabel('Вес')
    plt.title('Зависимость длины от веса')
    plt.show()

def make_length_weight_reg_plot():
    plt.figure(figsize=(10, 6))
    sns.regplot(data=dataset, x='Observed Length (m)', y='Observed Weight (kg)', scatter_kws={'alpha': 0.6})
    plt.title('Зависимость длины от веса с линией тренда')
    plt.show()

def make_contingency_matrix():
    contingency_table = pd.crosstab(dataset['Age Class'], dataset['Sex'])

    plt.figure(figsize=(10, 6))
    sns.heatmap(contingency_table, annot=True, fmt='d', cmap='Blues')
    plt.title('Тепловая карта таблицы сопряженности')
    plt.show()


make_length_histogram()
make_weight_histogram()
make_length_boxplot()
make_weight_boxplot()
make_sex_barplot()
make_region_barplot()
corr_matrix()
make_length_weight_scatter_plot()
make_length_weight_reg_plot()
make_contingency_matrix()


# Вывести всех крокодилов вида Morelet's Crocodile у которых вес больше 50кг
def find_crocodile():
    print(dataset[(dataset['Common Name'] == "Morelet's Crocodile") & (dataset['Observed Weight (kg)'] > 50)])

find_crocodile()