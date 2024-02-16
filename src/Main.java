import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    static final int STR_COUNT = 10_000;
    static final int STR_LENGTH = 100_000;
    static BlockingQueue<String> aQueue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> bQueue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> cQueue = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread fillThread = new Thread(() -> {
            String[] texts = new String[STR_COUNT];
            for (int i = 0; i < texts.length; i++) {
                texts[i] = generateText("abc", STR_LENGTH);
                try {
                    aQueue.put(texts[i]);
                    bQueue.put(texts[i]);
                    cQueue.put(texts[i]);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        AtomicReference<String> aStr = new AtomicReference<>("");
        AtomicInteger aMax = new AtomicInteger(0);
        Thread aThread = new Thread(() -> {
            String cur = "";
            for (int i = 0; i < STR_COUNT; i++) {
                try {
                    cur = aQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int curMax = (int) cur.chars().filter(ch -> ch == 'a').count();
                if (curMax > aMax.get()) {
                    aStr.set(cur);
                    aMax.set(curMax);
                    System.out.println("Max a: " + aMax);
                }
            }
        });
        AtomicReference<String> bStr = new AtomicReference<>("");
        AtomicInteger bMax = new AtomicInteger(0);
        Thread bThread = new Thread(() -> {
            String cur = "";
            for (int i = 0; i < STR_COUNT; i++) {
                try {
                    cur = bQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int curMax = (int) cur.chars().filter(ch -> ch == 'b').count();
                if (curMax > bMax.get()) {
                    bStr.set(cur);
                    bMax.set(curMax);
                    System.out.println("Max b: " + bMax);
                }
            }
        });
        AtomicReference<String> cStr = new AtomicReference<>("");
        AtomicInteger cMax = new AtomicInteger(0);
        Thread cThread = new Thread(() -> {
            String cur = "";
            for (int i = 0; i < STR_COUNT; i++) {
                try {
                    cur = cQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int curMax = (int) cur.chars().filter(ch -> ch == 'c').count();
                if (curMax > cMax.get()) {
                    cStr.set(cur);
                    cMax.set(curMax);
                    System.out.println("Max c: " + cMax);
                }
            }
        });

        fillThread.start();
        aThread.start();
        bThread.start();
        cThread.start();

        fillThread.join();
        aThread.join();
        bThread.join();
        cThread.join();

        System.out.println("Строка с самым большим количеством \'a\': " + aStr + " - " + aMax + " шт");
        System.out.println("Строка с самым большим количеством \'b\': " + bStr + " - " + bMax + " шт");
        System.out.println("Строка с самым большим количеством \'c\': " + cStr + " - " + cMax + " шт");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
