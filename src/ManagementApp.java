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

    private void saveData() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("#LECTURES\n");
            for (Lecture lecture : lectures) {
                writer.write(lecture.getId() + "|" + lecture.getYear() + "|" + lecture.getName() + "|" + lecture.getTime() + "\n");
            }

            writer.write("#STUDENTS\n");
            for (Student student : students) {
                writer.write(student.getId() + "|" + student.getLectureName() + "|" + student.getYear() + "|" + student.getName() + "|" + student.getStudentId() + "\n");
            }

            writer.write("#GRADES\n");
            for (Grade grade : grades) {
                writer.write(grade.getId() + "|" + grade.getLectureName() + "|" + grade.getYear() + "|" + grade.getStudentName() +
                        "|" + grade.getMidterm() + "|" + grade.getFinalExam() + "|" + grade.getAssignment() +
                        "|" + grade.getTotal() + "|" + grade.getAverage() + "\n");
            }
        }
    }

    private void loadData() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    section = line;
                } else {
                    String[] parts = line.split("\\|");
                    switch (section) {
                        case "#LECTURES":
                            lectures.add(new Lecture(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
                            break;
                        case "#STUDENTS":
                            students.add(new Student(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4]));
                            break;
                        case "#GRADES":
                            grades.add(new Grade(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                                    Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
                                    Integer.parseInt(parts[6]), Integer.parseInt(parts[7]),
                                    Double.parseDouble(parts[8])));
                            break;
                    }
                }
            }
        }
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
