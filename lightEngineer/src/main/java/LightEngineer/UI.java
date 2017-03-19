package LightEngineer;

import LightEngineer.IO.Interrupt;
import LightEngineer.IO.LightSwitch;
import LightEngineer.entities.Colour;
import LightEngineer.entities.ColourVariations;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by jackm on 07/11/2016.
 */
public class UI {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        UI ui = new UI(scanner);
        ui.welcomeMessage();

        int lightCount = ui.numberOfLightsQuestion();
        ArrayList<Colour> colours = ui.selectColour(new ArrayList<Colour>());
        LightSwitch lightSwitch = new LightSwitch(lightCount, colours);
        Interrupt interrupt = new Interrupt(lightSwitch, scanner);
        runProgram(lightSwitch, interrupt);

        Mutex mutex = new Mutex();
        while(true){
            mutex.acquire();
            if(!lightSwitch.getRunStatus()){
                Thread.sleep(1000);
                System.out.println("-------------------------------------");
                System.out.println("finish program? Y or N");
                System.out.println("-------------------------------------");
                if(getYOrNFromUser()){
                    System.out.println("Program Finished");
                    System.out.println("-------------------------------------");
                    mutex.release();
                    System.exit(0);
                }
                System.out.println("-------------------------------------");
                System.out.println("Change tbe number of lights? Y or N");
                System.out.println("-------------------------------------");
                if(getYOrNFromUser()){
                    lightCount = ui.getNumberOfLightsValue();
                }

                printCurrentColourList(lightSwitch);

                colours = ui.selectColour(lightSwitch.getColours());
                lightSwitch.setColours(colours);
                lightSwitch = new LightSwitch(lightCount, lightSwitch.getColours());
                interrupt = new Interrupt(lightSwitch, scanner);
                mutex.release();
                runProgram(lightSwitch,interrupt);
            }
            mutex.release();
        }


    }

    private static void printCurrentColourList(LightSwitch lightSwitch) {
        System.out.println("Colours currently selected for the lights");
        System.out.println("-------------------------------------");
        int colourCount = 1;
        for(Colour colour : lightSwitch.getColours()) {
            System.out.println(colourCount + ". " + colour.getColourName());
            colourCount++;
        }
        System.out.println("-------------------------------------");
    }

    private static void runProgram(LightSwitch lightSwitch, Interrupt interrupt) throws InterruptedException {
        Mutex mutex = new Mutex();
        mutex.acquire();
        System.out.println("-------------------------------------");
        System.out.println(" NOTICE*:- please type J followed by Enter or Return to interrupt the light show ");
        System.out.println("-------------------------------------");
        lightSwitch.start();
        mutex.release();
        while(true) {
            mutex.acquire();
            if (lightSwitch.getRunStatus()) {
                mutex.release();
                interrupt.start();
                break;
            }
            mutex.release();
        }
    }

    private static Scanner userInput;

    public UI(Scanner scanner) {
        this.userInput = scanner;
    }

    public static void PrintMessage(String logMessage) {
        System.out.println(logMessage);
    }

    public void welcomeMessage() {
        System.out.println("-------------------------------------");
        System.out.println(" Welcome to Jack's Light Program  ");
        System.out.println(" 1. please select your favoured options ");
        System.out.println(" 2. please type J followed by Enter or Return to interrupt the light show ");
        System.out.println("-------------------------------------");
    }

    public Integer numberOfLightsQuestion() {
        System.out.println("Would you like to provide a number of desired lights? Y/N");
        System.out.println("-------------------------------------");
        if (!getYOrNFromUser()) {
            System.out.println("-------------------------------------");
            System.out.println("The number of lights has been set to 20 by default.");
            return 20;
        }
        return getNumberOfLightsValue();
    }

    private Integer getNumberOfLightsValue() {
        System.out.println("-------------------------------------");
        System.out.println("Please enter the number of desired lights!");
        System.out.println("-------------------------------------");
        return getIntegerFromUser(100);
    }

    private static Integer getIntegerFromUser(Integer maxValue) {
        Integer value;
        do {
            while (!userInput.hasNextInt()) {
                System.out.println("-------------------------------------");
                System.out.println("|  provided value was incorrect!    |");
                System.out.println("-------------------------------------");
                userInput.next();
            }
            value = userInput.nextInt();
        } while (value <= 0 && value > maxValue);
        return value;
    }

    private static Boolean getYOrNFromUser() {
        String input = userInput.next();
        if (input.equalsIgnoreCase("Y")) {
            return true;
        } else if (input.equalsIgnoreCase("N")) {
            return false;
        }
        return false;
    }

    public static ArrayList<Colour> selectColour(ArrayList<Colour> colours) {
        ArrayList<Colour> userSelectedColours = colours;
        Integer userChoice = null;
        System.out.println("-------------------------------------");
        System.out.println("|       Colours for the lights      |");
        System.out.println("-------------------------------------");
        System.out.println("Please select one of the following options");
        System.out.println("-------------------------------------");
        System.out.println("   1. preset (RED,YELLOW,GREEN,BLUE)");
        System.out.println("   2. Add your own selection of colours");
        if(colours.size() > 0){
            System.out.println("   3. Randomise current colour order");
            System.out.println("   4. No changes");
            System.out.println("-------------------------------------");

            userChoice = getIntegerFromUser(4);
        }
        else{
            System.out.println("-------------------------------------");
            userChoice = getIntegerFromUser(2);
        }

        switch (userChoice) {
            case 1:
                userSelectedColours = getPresetColours();
                break;
            case 2:
                userSelectedColours = getCustomColours(colours);
                break;
            case 3:
                Collections.shuffle(userSelectedColours);
                break;
            case 4:
                return userSelectedColours;
        }

        return userSelectedColours;
    }

    private static ArrayList<Colour> getPresetColours() {
        ArrayList<Colour> colours = new ArrayList<Colour>();
        colours.add(ColourVariations.COLOUR_RED);
        colours.add(ColourVariations.COLOUR_YELLOW);
        colours.add(ColourVariations.COLOUR_GREEN);
        colours.add(ColourVariations.COLOUR_BLUE);
        return colours;
    }

    public static ArrayList<Colour> getCustomColours(ArrayList<Colour> colours) {
        ArrayList<Colour> userDefinedColourSet = new ArrayList<Colour>();
        if (colours != null) {
            userDefinedColourSet.addAll(colours);
        }

        ListPredefinedColoursOptions();
        Colour colour = userColourSelection();
        userDefinedColourSet.add(colour);
        System.out.println("-------------------------------------");
        System.out.println("Add another Colour? Y or N");
        while (getYOrNFromUser()) {
            ListPredefinedColoursOptions();
            colour = userColourSelection();
            userDefinedColourSet.add(colour);
            System.out.println("Add another Colour? Y or N");
        }
        return userDefinedColourSet;
    }

    private static Colour userColourSelection() {
        Integer integerFromUser = getIntegerFromUser(9);
        switch (integerFromUser) {
            case 1:
                return ColourVariations.COLOUR_RED;
            case 2:
                return ColourVariations.COLOUR_BLACK;
            case 3:
                return ColourVariations.COLOUR_BLUE;
            case 4:
                return ColourVariations.COLOUR_ORANGNE;
            case 5:
                return ColourVariations.COLOUR_GREEN;
            case 6:
                return ColourVariations.COLOUR_PURPLE;
            case 7:
                return ColourVariations.COLOUR_WHITE;
            case 8:
                return ColourVariations.COLOUR_YELLOW;
            case 9:
                return customColor();
        }
        return null;
    }

    private static Colour customColor() {
        System.out.println("Select R value");
        int r = getIntegerFromUser(255);
        System.out.println("Select G value");
        int g = getIntegerFromUser(255);
        System.out.println("Select B value");
        int b = getIntegerFromUser(255);
        return new Colour(r, g, b);
    }

    private static void ListPredefinedColoursOptions() {
        System.out.println("-------------------------------------");
        System.out.println(" 1. " + ColourVariations.COLOUR_RED.getColourName() +
                "   2. " + ColourVariations.COLOUR_BLACK.getColourName());
        System.out.println(" 3. " + ColourVariations.COLOUR_BLUE.getColourName() +
                "   4. " + ColourVariations.COLOUR_ORANGNE.getColourName());
        System.out.println(" 5. " + ColourVariations.COLOUR_GREEN.getColourName() +
                "   6. " + ColourVariations.COLOUR_PURPLE.getColourName());
        System.out.println(" 7. " + ColourVariations.COLOUR_WHITE.getColourName() +
                "   8. " + ColourVariations.COLOUR_YELLOW.getColourName());
        System.out.println(" 9. Add Custom Colour");
        System.out.println("-------------------------------------");
    }
}
