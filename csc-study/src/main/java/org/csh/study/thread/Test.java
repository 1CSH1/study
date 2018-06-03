package org.csh.study.thread;
public class Test {

    /**
     *
     * 子线程循环10次,接着主线程循环100次,接着又回到子线程循环10次, 接着再回到主线程又循环100次,如此循环50次.写出程序.
     *
     * @param args
     */

    public static void main(String[] args) {

        final Business business = new Business();
        for (int i = 1; i <= 50; i++) {
            final int run = i;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    business.sub(run);

                }
            }).start();

            business.main(i);
        }
    }

    static class Business {
        private boolean isSubTurn = true;

        public synchronized void sub(int i) {
            while (!isSubTurn) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int j = 1; j <= 10; j++) {
                System.out.println("sub run " + j + " in round " + i);
            }
            isSubTurn = false;
            this.notify();
        }

        public synchronized void main(int i) {
            while (isSubTurn) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int j = 1; j <= 100; j++) {
                System.out.println("main run " + j + " in round " + i);
            }
            isSubTurn = true;
            this.notify();
        }
    }
}