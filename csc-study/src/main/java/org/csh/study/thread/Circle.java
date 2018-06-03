package org.csh.study.thread;

import sun.text.normalizer.UBiDiProps;

public class Circle {

    public static void main(String[] args) {
        final Busicess busicess = new Busicess();
        SubThread subThread = new SubThread(busicess);
        subThread.start();
        for (int i = 0; i < 50; i++) {
            final int run = i;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    busicess.sub(run);
//                }
//            }).start();
            busicess.main(run);
        }
    }

    static class SubThread extends Thread {
        Busicess busicess;
        public SubThread(Busicess busicess) {
            this.busicess = busicess;
        }
        @Override
        public void run() {
            int i = 0;
            busicess.sub(i++);
        }
    }

    static class Busicess {

        private boolean isSubTurn = true;

        public synchronized void sub(int run) {
            while (!isSubTurn) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 50; i++) {
                    System.out.println("sub run " + i + " in round " + run);
                }
                isSubTurn = false;
                this.notify();
            }
        }

        public synchronized void main(int run) {
            while (isSubTurn) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 100; i++) {
                System.out.println("main run " + i + " in round " + run);
            }
            isSubTurn = true;
            this.notify();
        }
    }
}
