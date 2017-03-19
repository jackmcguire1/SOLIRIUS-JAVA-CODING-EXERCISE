package LightEngineer.IO;

import LightEngineer.UI;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.util.Scanner;

public class Interrupt extends Thread {

    private LightSwitch lightSwitch;
    private Scanner scanner;

    public Interrupt(LightSwitch lightSwitch, Scanner scanner) {
        this.lightSwitch = lightSwitch;
        this.scanner = scanner;
    }

    public void run() {
        Mutex mutex = new Mutex();
        try {
            mutex.acquire();
            if(scanner.hasNext()){
                if(scanner.next().equals("j")){
                    lightSwitch.setRunStatus(false);
                    UI.PrintMessage("Light Show Interrupted");
                    this.join();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mutex.release();
    }
}
