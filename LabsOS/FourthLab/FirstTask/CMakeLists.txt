cmake_minimum_required(VERSION 3.29)
project(FirstTask)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
add_executable(FirstTask main.cpp
        memory_manager.h)
set(GTEST_DIR ${CMAKE_SOURCE_DIR}/googletest)

add_executable(
        tests_for_lab
        TestsForLab.cpp
        memory_manager.h
)

add_subdirectory(${GTEST_DIR})
target_link_libraries(
        tests_for_lab
        gtest
        gtest_main
)

enable_testing()


# Линкуем тесты с библиотекой Google Test
target_link_libraries(
        tests_for_lab
        GTest::gtest_main
)

# Автоматическое обнаружение тестов
include(GoogleTest)
gtest_discover_tests(tests_for_lab)
