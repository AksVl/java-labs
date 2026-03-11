package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Core core = new Core(System.in, System.out);
        core.start();
    }
}
