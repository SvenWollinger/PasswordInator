package net.svenwollinger.passwordinator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MainWindow extends JFrame {

    private ArrayList<String> passwordList = new ArrayList<>();
    private String faqText = "";

    private JTextField passwd = new JTextField("Password");
    private JCheckBox useNumbers = new JCheckBox("Uncrackable?");
    private int length;

    private final int numbers = 2;

    private final int minimum = 6;
    private final int maximum = 12;

    public MainWindow () throws IOException {
        loadFile("psswd.txt", "psswd");
        loadFile("faq.txt", "faq");

        final int wWidth = 512;
        final int wHeight = 256;

        final int wX = (int) ((Main.screenSize.getWidth()/2) - (wWidth/2));
        final int wY = (int) ((Main.screenSize.getHeight()/2) - (wHeight/2));

        this.setSize(wWidth, wHeight);
        this.setTitle("Password Inator");
        this.setLocation(wX,wY);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupContent();

        this.setResizable(false);
        this.setVisible(true);
    }

    private void setupContent() {
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0,1));

        JLabel title = new JLabel("Password Inator", SwingConstants.RIGHT);
        title.setFont(new Font("Consolas", Font.BOLD, 25));
        content.add(createRow( title, new JLabel("   by Sven Wollinger")));

        passwd = new JTextField("Password");
        passwd.setHorizontalAlignment(SwingConstants.CENTER);
        passwd.setEditable(false);
        content.add(createRow( passwd));

        JLabel psswdLengthLabel = new JLabel();
        JSlider psswdLength = new JSlider();
        psswdLength.setMinimum(minimum);
        psswdLength.setMaximum(maximum);

        psswdLength.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                psswdLengthLabel.setText("   " + slider.getValue() + " Characters");
                length = slider.getValue();
                passwd.setText(genPsswd(length, useNumbers.isSelected()));
            }
        });
        content.add(createRow( psswdLength, psswdLengthLabel));
        psswdLength.setValue(minimum);

        useNumbers.setVerticalAlignment(SwingConstants.CENTER);
        useNumbers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwd.setText(genPsswd(length, useNumbers.isSelected()));
            }
        });

        JButton openFAQ = new JButton("FAQ");
        openFAQ.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame faq = new JFrame();
                faq.add(new JLabel(faqText));
                faq.pack();
                faq.setTitle("FAQ");
                final int wX = (int) ((Main.screenSize.getWidth()/2) - (faq.getWidth()/2));
                final int wY = (int) ((Main.screenSize.getHeight()/2) - (faq.getHeight()/2));
                faq.setLocation(wX,wY);
                faq.setVisible(true);
            }
        });

        content.add(createRow(useNumbers, openFAQ));

        JButton genButton = new JButton("Generate Password");
        genButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwd.setText(genPsswd(length, useNumbers.isSelected()));
            }
        });
        content.add(genButton);

        this.add(content);
    }

    public JPanel createRow(JComponent... components) {
        JPanel panel = new JPanel(new GridLayout(0, components.length));
        for(JComponent component : components)
            panel.add(component);
        return panel;
    }

    public String genPsswd(int length, boolean useNumbers){
        final int min = 0;
        final int max = passwordList.size() - 1;

        final int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        int newLength = length;
        if(useNumbers)
            newLength -= numbers;

        String psswd = passwordList.get(randomNum); //Original password
        String newPsswd = ""; //Edited password containing numbers, maybe shorter or longer
        if(length < psswd.length()) {
            //Password is longer then the chosen length, crop it
            for(int i = 0; i < newLength; i++)
                newPsswd += psswd.charAt(i);
        } else if(length > psswd.length()) {
            //Password is shorter then the chosen length, extend it
            for(int i = 0; i < newLength; i++) {
                if(i < psswd.length())
                    newPsswd += psswd.charAt(i);
                else
                    newPsswd += psswd.charAt(psswd.length()-1);
            }
        } else if(length == psswd.length()) {
            //Password is equal. Possibly crop it (if newlength != password length)
            for(int i = 0; i < newLength; i++)
                newPsswd += psswd.charAt(i);
        }

        if(useNumbers) {
            for(int i = 0; i < numbers; i++) {
                newPsswd += ThreadLocalRandom.current().nextInt(0, 9);
            }
        }

        return newPsswd;
    }

    public void loadFile(String path, String type) throws IOException {
        InputStream inputStream = ClassLoader.getSystemClassLoader().getSystemResourceAsStream(path);
        InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader in = new BufferedReader(streamReader);

        for (String line; (line = in.readLine()) != null;) {
            if(type.equals("psswd"))
                passwordList.add(line);
            else if(type.equals("faq"))
                faqText += line;
        }

        inputStream.close();
        streamReader.close();
    }

}
