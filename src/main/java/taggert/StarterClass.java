package taggert;

import gnu.io.*;
import lombok.Setter;
import net.java.games.input.Controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;

@Setter
public class StarterClass extends Thread {
    boolean myInterrupt;

    public StarterClass() {
        super();
        myInterrupt = false;
    }

    @Override
    public void interrupt() {
        myInterrupt = true;
    }

    @Override
    public boolean isInterrupted() {
        return myInterrupt;
    }

    @Override
    public void run() {
        JInputJoystick joystick = new JInputJoystick(Controller.Type.STICK);

        if (joystick.isControllerConnected()) {
            App.controller.print(joystick.getControllerName() + " Found");
        } else {
            App.controller.print("Controller not found");
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
        while (!isInterrupted() && check) {
            index:
            check = false;
            App.controller.print("Choose your port and input it's position in the list below:");
            ports.forEach(x -> App.controller.print((ports.indexOf(x)+1)+". Port " + x + " "));
            s = App.controller.getInput();
            if (s == null) return;
            if (Integer.parseInt(s) > ports.size() || Integer.parseInt(s) <= 0 || s.isEmpty()) {
                App.controller.print("Wrong input");
                check = true;
            }
            if (!check) {
                App.controller.print("You've chosen port: " + ports.get(Integer.parseInt(s) - 1));
                try {
                    connect = connect(ports.get(Integer.parseInt(s) - 1), 9600, 8, 1, 0);
                } catch (PortInUseException e) {
                    check = true;
                }
            }
        }
        try (OutputStream outputStream = connect.getOutputStream();
             PrintStream writer = new PrintStream(outputStream)) {
            while (!isInterrupted() && joystick.isControllerConnected()) {
                String xValue = "0";
                String yValue = "0";
                if (joystick.getXAxisPercentage() > 60) {
                    xValue = "2";

                } else if (joystick.getXAxisPercentage() < 40) {
                    xValue = "1";
                }
                if (joystick.getYAxisPercentage() > 60) {
                    yValue = "2";
                } else if (joystick.getYAxisPercentage() < 40) {
                    yValue = "1";
                }
                String pocket = xValue + yValue;
                if (!pocket.equals(mValue)) {
                    App.controller.light(xValue, yValue);
                    mValue = pocket;
                    //App.controller.print(mValue);
                    writer.println(mValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        connect.close();
    }

    private static RXTXPort connect(String portName, int baud, int data, int stop, int parity) throws PortInUseException {
        RXTXPort port = null;
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            port = (RXTXPort) portIdentifier.open(portName, 2000);
        } catch (NoSuchPortException ex) {
            App.controller.print("Unable to open connection with " + portName);
        } catch (PortInUseException ex) {
            System.err.println("Unable to open connection with " + portName);
            throw new PortInUseException();
        }

        if (port != null) {
            if (port.getBaudRate() != baud || port.getDataBits() != data || port.getStopBits() != stop || port.getParity() != parity) {
                try {
                    port.setSerialPortParams(baud, data, stop, parity);
                } catch (UnsupportedCommOperationException ex) {
                    App.controller.print("Unable to configure  connection with " + portName);
                    port = null;
                }
            }
        }

        return port;
    }
}