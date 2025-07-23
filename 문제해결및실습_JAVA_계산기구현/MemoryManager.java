package org.prosoljava.assignment;

/**
 * 계산기 Class에서 사용하는 메모리를 관리하는 class
 *
 * @author 김민우
 * @version 1.0
 * @since 2024-11-26~
 */
public class MemoryManager {
    private double memory;

    /**
     * 생성자
     *
     */
    public MemoryManager() {
        this.memory = 0.0; // 초기 메모리 값
    }

    /**
     *  메모리 초기화 (MC)
     *
     */
    public void clearMemory() {
        memory = 0.0;
    }

    /**
     * 메모리 값 읽기 (MR)
     *
     * @return 현재 메모리값
     */
    public double recallMemory() {
        return memory;
    }

    /**
     * 현재값 메모리에 더하기 (M+)
     *
     * @param value 계산기에서 M+를 눌렀을 때 숫자
     */
    public void addToMemory(double value) {
        memory += value;
    }
}
