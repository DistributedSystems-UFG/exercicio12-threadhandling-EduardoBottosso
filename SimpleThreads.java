public class SimpleThreads {

    // Display a message, preceded by the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };

            try {
                for (int i = 0; i < importantInfo.length; i++) {
                    Thread.sleep(4000);
                    threadMessage(importantInfo[i]);
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    private static class CpuIntensive implements Runnable {
        public void run() {
            threadMessage("CPU-intensive task started");

            long number = 2;
            long primesFound = 0;

            while (true) {

                // Verifica se esta thread foi interrompida
                if (Thread.currentThread().isInterrupted()) {
                    threadMessage("CPU-intensive task was interrupted. Finishing...");
                    return;
                }

                if (isPrime(number)) {
                    primesFound++;
                }

                number++;

                // Só para mostrar que a thread continua trabalhando
                if (number % 1_000_000 == 0) {
                    threadMessage("Primes found so far: " + primesFound);
                }
            }
        }

        private boolean isPrime(long n) {
            if (n < 2) {
                return false;
            }

            for (long i = 2; i * i <= n; i++) {
                if (n % i == 0) {
                    return false;
                }
            }

            return true;
        }
    }

    public static void main(String args[]) throws InterruptedException {

        // Delay, in milliseconds before we interrupt the threads
        long patience = 1000 * 60 * 60;

        // If command line argument present, gives patience in seconds
        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Starting MessageLoop thread");
        Thread messageThread = new Thread(new MessageLoop(), "MessageLoop");

        threadMessage("Starting CPU-intensive thread");
        Thread cpuThread = new Thread(new CpuIntensive(), "CpuIntensive");

        long startTime = System.currentTimeMillis();

        messageThread.start();
        cpuThread.start();

        threadMessage("Waiting for threads to finish");

        while (messageThread.isAlive() || cpuThread.isAlive()) {
            threadMessage("Still waiting...");

            // Espera um pouco por cada thread
            messageThread.join(500);
            cpuThread.join(500);

            if ((System.currentTimeMillis() - startTime) > patience) {
                threadMessage("Tired of waiting!");

                if (messageThread.isAlive()) {
                    threadMessage("Interrupting MessageLoop thread");
                    messageThread.interrupt();
                }

                if (cpuThread.isAlive()) {
                    threadMessage("Interrupting CPU-intensive thread");
                    cpuThread.interrupt();
                }

                messageThread.join();
                cpuThread.join();
            }
        }

        threadMessage("Finally!");
    }
}
