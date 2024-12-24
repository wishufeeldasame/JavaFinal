import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Lecture {
    private int id;
    private String year;
    private String name;
    private String time;

    public Lecture(int id, String year, String name, String time) {
        this.id = id;
        this.year = year;
        this.name = name;
        this.time = time;
    }

    public int getId() { return id; }
    public String getYear() { return year; }
    public String getName() { return name; }
    public String getTime() { return time; }
}

class Student {
    private int id;
    private String lectureName;
    private String year;
    private String name;
    private String studentId;

    public Student(int id, String lectureName, String year, String name, String studentId) {
        this.id = id;
        this.lectureName = lectureName;
        this.year = year;
        this.name = name;
        this.studentId = studentId;
    }

    public int getId() { return id; }
    public String getLectureName() { return lectureName; }
    public String getYear() { return year; }
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
}

class Grade {
    private int id;
    private String lectureName;
    private String year;
    private String studentName;
    private int midterm, finalExam, assignment, total;
    private double average;

    public Grade(int id, String lectureName, String year, String studentName, int midterm, int finalExam, int assignment) {
        this.id = id;
        this.lectureName = lectureName;
        this.year = year;
        this.studentName = studentName;
        this.midterm = midterm;
        this.finalExam = finalExam;
        this.assignment = assignment;
        this.total = midterm + finalExam + assignment;
        this.average = total / 3.0;
    }

    public int getId() { return id; }
    public String getLectureName() { return lectureName; }
    public String getYear() { return year; }
    public String getStudentName() { return studentName; }
    public int getMidterm() { return midterm; }
    public int getFinalExam() { return finalExam; }
    public int getAssignment() { return assignment; }
    public int getTotal() { return total; }
    public double getAverage() { return average; }
}
class ManagementApp extends JFrame {
    private java.util.List<Lecture> lectures = new ArrayList<>();
    private java.util.List<Student> students = new ArrayList<>();
    private java.util.List<Grade> grades = new ArrayList<>();
    private final String FILE_NAME = "data.txt";

    private LecturePanel lecturePanel;
    private StudentPanel studentPanel;
    private GradePanel gradePanel;
    private GradeQueryPanel gradeQueryPanel;

    public ManagementApp() {
        setTitle("성적 관리 프로그램");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 메뉴바 추가
        createMenuBar();

        // 탭 패널 구성
        JTabbedPane tabbedPane = new JTabbedPane();
        lecturePanel = new LecturePanel(lectures);
        studentPanel = new StudentPanel(students);
        gradePanel = new GradePanel(grades, students, lectures);
        gradeQueryPanel = new GradeQueryPanel();

        tabbedPane.addTab("강의 관리", lecturePanel);
        tabbedPane.addTab("학생 관리", studentPanel);
        tabbedPane.addTab("성적 등록", gradePanel);
        tabbedPane.addTab("성적 조회", gradeQueryPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // 초기 데이터 로드
        try {
            loadData();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 로드 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 파일 메뉴
        JMenu fileMenu = new JMenu("파일");
        JMenuItem saveItem = new JMenuItem("저장");
        JMenuItem loadItem = new JMenuItem("불러오기");
        JMenuItem exitItem = new JMenuItem("종료");

        saveItem.addActionListener(e -> saveToFile());
        loadItem.addActionListener(e -> loadFromFile());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 도움말 메뉴
        JMenu helpMenu = new JMenu("도움말");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        // 메뉴바에 메뉴 추가
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "성적 관리 프로그램 v1.0\n" +
                        "제작자: Seojangho\n" +
                        "기능: 강의, 학생, 성적 관리를 위한 프로그램",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // Save lectures
            writer.write("#LECTURES\n");
            for (Lecture lecture : lectures) {
                writer.write(lecture.getId() + "|" + lecture.getYear() + "|" + lecture.getName() + "|" + lecture.getTime() + "\n");
            }

            // Save students
            writer.write("#STUDENTS\n");
            for (Student student : students) {
                writer.write(student.getId() + "|" + student.getLectureName() + "|" + student.getYear() + "|" + student.getName() + "|" + student.getStudentId() + "\n");
            }

            // Save grades
            writer.write("#GRADES\n");
            for (Grade grade : grades) {
                writer.write(grade.getId() + "|" + grade.getLectureName() + "|" + grade.getYear() + "|" + grade.getStudentName() +
                             "|" + grade.getMidterm() + "|" + grade.getFinalExam() + "|" + grade.getAssignment() +
                             "|" + grade.getTotal() + "|" + grade.getAverage() + "\n");
            }

            JOptionPane.showMessageDialog(this, "파일 저장이 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 저장 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            lectures.clear();
            students.clear();
            grades.clear();
    
            String line;
            String currentSection = "";
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    currentSection = line;
                } else {
                    String[] parts = line.split("\\|");
                    switch (currentSection) {
                        case "#LECTURES":
                            lectures.add(new Lecture(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
                            break;
                        case "#STUDENTS":
                            students.add(new Student(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4]));
                            break;
                        case "#GRADES":
                            grades.add(new Grade(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                                                 Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
                                                 Integer.parseInt(parts[6])));
                            break;
                    }
                }
            }
    
            // 각 패널의 테이블 갱신
            lecturePanel.loadLecturesToTable();
            studentPanel.loadStudentsToTable();
    
            JOptionPane.showMessageDialog(this, "파일 로드가 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 로드 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void loadData() throws IOException {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            loadFromFile();
        }
    }
}

class LecturePanel extends JPanel {
    private JTable lectureTable;
    private DefaultTableModel tableModel;
    private java.util.List<Lecture> lectures;

    public LecturePanel(java.util.List<Lecture> lectures) {
        this.lectures = lectures;
        setLayout(new BorderLayout());

        // 테이블 생성
        String[] columns = {"ID", "기준년도", "강의명", "시간"};
        tableModel = new DefaultTableModel(columns, 0);
        lectureTable = new JTable(tableModel);
        add(new JScrollPane(lectureTable), BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("강의 추가");
        JButton deleteButton = new JButton("강의 삭제");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        loadLecturesToTable();
    }

    void loadLecturesToTable() {
        tableModel.setRowCount(0);
        for (Lecture lecture : lectures) {
            tableModel.addRow(new Object[]{
                lecture.getId(),
                lecture.getYear(),
                lecture.getName(),
                lecture.getTime()
            });
        }
    }
}

class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private java.util.List<Student> students;

    public StudentPanel(java.util.List<Student> students) {
        this.students = students;
        setLayout(new BorderLayout());

        // 테이블 생성
        String[] columns = {"ID", "강의명", "기준년도", "이름", "학번"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("학생 추가");
        JButton deleteButton = new JButton("학생 삭제");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        loadStudentsToTable();
    }

    void loadStudentsToTable() {
        tableModel.setRowCount(0);
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                student.getId(),
                student.getLectureName(),
                student.getYear(),
                student.getName(),
                student.getStudentId()
            });
        }
    }
}

class GradePanel extends JPanel {
    private java.util.List<Grade> grades;
    private java.util.List<Student> students;
    private java.util.List<Lecture> lectures;

    public GradePanel(java.util.List<Grade> grades, java.util.List<Student> students, java.util.List<Lecture> lectures) {
        this.grades = grades;
        this.students = students;
        this.lectures = lectures;

        // UI 초기화 (테이블, 필터 추가)
    }


}

class GradeQueryPanel extends JPanel {
    private java.util.List<Grade> grades;


}



public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManagementApp();
        });
    }
}