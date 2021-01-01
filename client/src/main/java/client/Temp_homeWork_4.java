package client;

public class Temp_homeWork_4 {
    Object mon = new Object();
    private static char LETTER = 'A';

    public static void main(String[] args) {
        Temp_homeWork_4 temp_homeWork = new Temp_homeWork_4();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                temp_homeWork.printA();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                temp_homeWork.printB();
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                temp_homeWork.printC();
            }
        });
        t1.start();
        t2.start();
        t3.start();
    }

    public void printA() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (LETTER != 'A') {
                        mon.wait();
                    }
                    System.out.print("A");
                    LETTER = 'B';
                    mon.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void printB() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (LETTER != 'B') {
                        mon.wait();
                    }
                    System.out.print("B");
                    LETTER = 'C';
                    mon.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void printC() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (LETTER != 'C') {
                        mon.wait();
                    }
                    System.out.println("C");
                    LETTER = 'A';
                    mon.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
