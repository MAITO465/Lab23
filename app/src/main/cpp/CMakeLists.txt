cmake_minimum_required(VERSION 3.22.1)

project("maitolab23")

add_library(
        maito-shield-lib
        SHARED
        maito-shield-lib.cpp)

find_library(
        log-lib
        log)

target_link_libraries(
        maito-shield-lib
        ${log-lib})
