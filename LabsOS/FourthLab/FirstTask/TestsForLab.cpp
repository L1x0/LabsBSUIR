#include <gtest/gtest.h>
#include "memory_manager.h"

TEST(MemoryManagerTest, AllocateDeallocate) {
    MemoryManager manager(1024);

    void* block1 = manager.allocate(128);
    void* block2 = manager.allocate(256);

    ASSERT_NE(block1, nullptr);
    ASSERT_NE(block2, nullptr);

    manager.deallocate(block1);
    manager.deallocate(block2);

    ASSERT_NO_THROW(manager.printStatus());
}

TEST(MemoryManagerTest, WriteRead) {
    MemoryManager manager(512);

    void* block = manager.allocate(128);

    std::string data = "Test Data";
    manager.write(block, data.c_str(), data.size() + 1);

    char buffer[128];
    manager.read(block, buffer, data.size() + 1);

    ASSERT_STREQ(buffer, data.c_str());

    manager.deallocate(block);
}

TEST(MemoryManagerTest, AllocateMoreThanAvailable) {
    MemoryManager manager(128);

    ASSERT_THROW(manager.allocate(256), std::runtime_error);
}

TEST(MemoryManagerTest, WriteExceedsBlock) {
    MemoryManager manager(128);

    void* block = manager.allocate(64);
    char data[128] = {0};

    ASSERT_THROW(manager.write(block, data, 128), std::runtime_error);

    manager.deallocate(block);
}

TEST(MemoryManagerTest, DoubleDeallocate) {
    MemoryManager manager(128);

    void* block = manager.allocate(64);
    manager.deallocate(block);

    ASSERT_THROW(manager.deallocate(block), std::runtime_error);
}

TEST(MemoryManagerTest, PrintStatus) {
    MemoryManager manager(512);

    void* block1 = manager.allocate(128);
    void* block2 = manager.allocate(64);

    testing::internal::CaptureStdout();
    manager.printStatus();
    std::string output = testing::internal::GetCapturedStdout();

    ASSERT_TRUE(output.find("128 байт") != std::string::npos);
    ASSERT_TRUE(output.find("64 байт") != std::string::npos);

    manager.deallocate(block1);
    manager.deallocate(block2);
}