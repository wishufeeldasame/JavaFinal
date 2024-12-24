import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.io.*;
import java.util.*;

public class ManagementApp extends JFrame {
    private List<Lecture> lectures = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Grade> grades = new ArrayList<>();
    private final String FILE_NAME = "data.txt";

    public ManagementApp() {
        setTitle("성적 관리 프로그램");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        setVisible(true);
    }
}
