package taggert;

import gnu.io.*;
import net.java.games.input.Controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * made by taggert
 */
public class App {
    public static void main(String[] args) {

        JInputJoystick joystick = new JInputJoystick(Controller.Type.STICK);

        if (joystick.isControllerConnected()) {
            System.out.println(joystick.getControllerName() + " Found");
        } else {
            System.out.println("Controller not found");
            return;
        }

        String mValue = "";

        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> ports = new ArrayList<>();
        while (portIdentifiers.hasMoreElements()) {
            CommPortIdentifier cpi = (CommPortIdentifier) portIdentifiers.nextElement();
            ports.add(cpi.getName());
        }
        boolean check = true;
        String s = "";
        RXTXPort connect = null;
        while (check) {
         index:   check = false;
            System.out.println("Choose your port and input it's number:");
            ports.forEach(x -> System.out.println("Port " + x + " "));
            Scanner scanner = new Scanner(System.in);
            s = scanner.nextLine();
            if (Integer.parseInt(s) > ports.size() || Integer.parseInt(s) <= 0||s.isEmpty()) {
                System.out.println("Wrong input");
                check = true;
            }
            if (!check) {
                System.out.println("You've chosen port: " + ports.get(Integer.parseInt(s) - 1));
                try {
                    connect = connect(ports.get(Integer.parseInt(s) - 1), 9600, 8, 1, 0);
                } catch (PortInUseException e) {
                    check = true;
                }
            }
        }
        OutputStream outputStream = connect.getOutputStream();
        PrintStream writer = new PrintStream(outputStream);

        while (joystick.isControllerConnected() && check(joystick)) {
            String xValue = "0";
            String yValue = "0";
            if (joystick.getXAxisPercentage() > 52) {
                xValue = "2";

            } else if (joystick.getXAxisPercentage() < 48) {
                xValue = "1";
            }
            if (joystick.getYAxisPercentage() > 52) {
                yValue = "2";
            } else if (joystick.getYAxisPercentage() < 48) {
                yValue = "1";
            }
            String pocket = xValue + yValue;
            if (!pocket.equals(mValue)) {
                mValue = pocket;
                System.out.println(mValue);
                writer.println(mValue);
            }
        }

        try {
            outputStream.close();
            connect.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static boolean check(JInputJoystick joystick) {
        if (joystick.getXAxisPercentage() == 100 && joystick.getYAxisPercentage() == 100) {
            return false;
        }
        return true;
    }

    private static RXTXPort connect(String portName, int baud, int data, int stop, int parity) throws PortInUseException {
        RXTXPort port = null;
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            port = (RXTXPort) portIdentifier.open(portName, 2000);
        } catch (NoSuchPortException ex) {
            System.out.println("Unable to open connection with " + portName);
        } catch (PortInUseException ex) {
            System.err.println("Unable to open connection with " + portName);
            throw new PortInUseException();
        }

        if (port != null) {
            if (port.getBaudRate() != baud || port.getDataBits() != data || port.getStopBits() != stop || port.getParity() != parity) {
                try {
                    port.setSerialPortParams(baud, data, stop, parity);
                } catch (UnsupportedCommOperationException ex) {
                    System.out.println("Unable to configure  connection with " + portName);
                    port = null;
                }
            }
        }

        return port;
    }
}
