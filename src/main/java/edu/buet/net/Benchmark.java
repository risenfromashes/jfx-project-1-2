package edu.buet.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.buet.util.Test;

class TestMessage implements Serializable {
    String message = "Hello! Hello! Testing 1 2 3 4! Hello! Thank you!";
    List<Integer> numbers = new ArrayList<>();
    public TestMessage() {
        for (int i = 0; i < 10; i++)
            numbers.add(i + 1);
    }
}
class BenchmarkServer {
    public static void main(String[] args) {
        try {
            new Server()
            .on(String.class, (m, s)-> {
                Test.assertEquals(m.getClass(), String.class);
                Test.assertEquals(m, "Ping");
                s.send("Pong", TestMessage.class)
                .thenCompose( res -> {
                    var t = new TestMessage();
                    Test.assertEquals(res.getClass(), TestMessage.class);
                    Test.assertEquals(res.message, t.message);
                    Test.assertEquals(res.numbers.size(), t.numbers.size());
                    for (int i = 0; i < res.numbers.size(); i++) {
                        Test.assertEquals(res.numbers.get(i), t.numbers.get(i));
                        t.numbers.set(i, t.numbers.get(i) * t.numbers.get(i));
                    }
                    return s.send(t, String.class);
                })
                .thenCompose(res -> {
                    Test.assertEquals(res.getClass(), String.class);
                    Test.assertEquals(res, "Noice, Server");
                    return s.send("Noice, Client");
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
            })
            .run(3001);
        } catch (IOException e) {
            System.out.println("Couldn't create server: " + e.getMessage());
        }
    }
}
class BenchmarkClient {
    private static double lastProgressReport = 0.0;
    private static long start = 0L;
    private static int N, M;
    private static DecimalFormat progressFormat = new DecimalFormat("##.00");
    private static int testsDone = 0;
    public static synchronized void incrementProgress() {
        testsDone++;
        double progress = (double)testsDone / N / M;
        if ((progress - lastProgressReport) > 0.005) {
            lastProgressReport = progress;
            var progressStr = progressFormat.format(progress * 100);
            System.out.print("\r" + progressStr  + "%");
        }
        if (progress >= 1.0) {
            System.out.print("\r100.00%");
            var elapsed = System.currentTimeMillis() - start;
            System.out.println("\nTested " + N + " * " + M + " echo roundtrips in " + elapsed + "ms with " + M + " concurrent connections");
        }
    }
    public static void main(String[] args) {
        start = System.currentTimeMillis();
        N = Integer.parseInt(args[0]);
        M = Integer.parseInt(args[1]);
        try {
            var client = new Client();
            client.onConnect( s -> {
                for (int j = 0; j < N; j++) {
                    s.send("Ping", String.class)
                    .thenCompose( res -> {
                        Test.assertEquals(res.getClass(), String.class);
                        Test.assertEquals(res, "Pong");
                        return s.send(new TestMessage(), TestMessage.class);
                    })
                    .thenCompose( res -> {
                        var t = new TestMessage();
                        Test.assertEquals(res.getClass(), TestMessage.class);
                        Test.assertEquals(res.message, t.message);
                        Test.assertEquals(res.numbers.size(), t.numbers.size());
                        for (int i = 0; i < res.numbers.size(); i++)
                            Test.assertEquals(res.numbers.get(i), t.numbers.get(i) * t.numbers.get(i));
                        return s.send("Noice, Server", String.class);
                    })
                    .thenAccept( res -> {
                        Test.assertEquals(res.getClass(), String.class);
                        Test.assertEquals(res, "Noice, Client");
                        BenchmarkClient.incrementProgress();
                    })
                    .exceptionally( e -> {
                        e.printStackTrace();
                        return null;
                    });
                }
            });
            for (int i = 0; i < M; i++)
                client.connect(InetAddress.getLocalHost(), 3001);
        } catch (IOException e) {
            System.out.println("Couldn't run test: " + e.getMessage());
        }
    }
}
