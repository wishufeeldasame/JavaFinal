import javax.swing.*;
import javax.swing.border.Border;

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

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("강의 관리", new LecturePanel());
        tabbedPane.addTab("학생 관리", new StudentPanel());
        tabbedPane.addTab("성적 등록", new GradePanel());
        tabbedPane.addTab("성적 조회", new GradeQueryPanel());

        add(tabbedPane, BorderLayout.CENTER);

        try{
            loadData();
        } catch (IOException e){
            JOptionPane.showMessageDialog(this, "파일 로드 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    private void saveData() throws IOException{

    }

    private void loadData() throws IOException{

    }

    class LecturePanel extends JPanel{

    }

    class StudentPanel extends JPanel{

    }

    class GradePanel extends JPanel{

    }

    class GradeQueryPanel extends JPanel{

    }

}
