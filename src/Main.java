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
        this.average = Math.round((total / 3.0) * 100) / 100.0;
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

    public void setMidterm(int midterm) {this.midterm = midterm;}
    public void setFinalExam(int finalExam) {this.finalExam = finalExam;}
    public void setAssignment(int assignment) {this.assignment = assignment;}
    public void setTotal(int total) {this.total = total;}
    public void setAverage(double average) {this.average = average;}
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
        gradeQueryPanel = new GradeQueryPanel(grades,lectures);

        tabbedPane.addTab("강의 관리", lecturePanel);
        tabbedPane.addTab("학생 관리", studentPanel);
        tabbedPane.addTab("성적 등록", gradePanel);
        tabbedPane.addTab("성적 조회", gradeQueryPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // 초기 데이터 로드
        try {
            loadData();
            gradePanel.updateLectureComboBox();
            gradeQueryPanel.updateYearComboBox();
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

        // 디버그 메시지 추가
        System.out.println("강의 로드 완료: " + lectures.size() + "개");
        System.out.println("학생 로드 완료: " + students.size() + "개");
        System.out.println("성적 로드 완료: " + grades.size() + "개");

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
    private JTable gradeTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> lectureComboBox;
    private JComboBox<String> studentComboBox;
    private JTextField midtermField, finalExamField, assignmentField;

    public GradePanel(java.util.List<Grade> grades, java.util.List<Student> students, java.util.List<Lecture> lectures) {
        this.grades = grades;
        this.students = students;
        this.lectures = lectures;

        setLayout(new BorderLayout());

        // 테이블 생성
        String[] columns = {"ID", "강의명", "기준년도", "학생명", "중간고사", "기말고사", "과제", "총점", "평균"};
        tableModel = new DefaultTableModel(columns, 0);
        gradeTable = new JTable(tableModel);
        add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        // 입력 패널 생성
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // 강의 선택
        inputPanel.add(new JLabel("강의:"));
        lectureComboBox = new JComboBox<>();
        lectureComboBox.addActionListener(e -> updateStudentComboBox());
        inputPanel.add(lectureComboBox);

        // 학생 선택
        inputPanel.add(new JLabel("학생:"));
        studentComboBox = new JComboBox<>();
        inputPanel.add(studentComboBox);

        // 중간고사 성적
        inputPanel.add(new JLabel("중간고사:"));
        midtermField = new JTextField();
        inputPanel.add(midtermField);

        // 기말고사 성적
        inputPanel.add(new JLabel("기말고사:"));
        finalExamField = new JTextField();
        inputPanel.add(finalExamField);

        // 과제 성적
        inputPanel.add(new JLabel("과제:"));
        assignmentField = new JTextField();
        inputPanel.add(assignmentField);

        add(inputPanel, BorderLayout.NORTH);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("성적 추가");
        addButton.addActionListener(e -> addGrade());
        buttonPanel.add(addButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        updateLectureComboBox();
    }

    void updateLectureComboBox() {
        lectureComboBox.removeAllItems();
        for (Lecture lecture : lectures) {
            lectureComboBox.addItem(lecture.getName());
        }

        if (lectureComboBox.getItemCount() > 0) {
            lectureComboBox.setSelectedIndex(0);
            updateStudentComboBox();
        } else {
            System.out.println("강의 목록이 비어 있습니다.");
        }
    }

    private void updateStudentComboBox() {
        studentComboBox.removeAllItems();
        String selectedLecture = (String) lectureComboBox.getSelectedItem();
        System.out.println("선택된 강의: " + selectedLecture);

        if (selectedLecture != null) {
            for (Student student : students) {
                if (student.getLectureName().equals(selectedLecture)) {
                    studentComboBox.addItem(student.getName());
                }
            }
        }

        if (studentComboBox.getItemCount() == 0) {
            System.out.println("선택된 강의에 등록된 학생이 없습니다.");
        }
    }

    public void loadGradesToTable() {
        tableModel.setRowCount(0);
        for (Grade grade : grades) {
            tableModel.addRow(new Object[]{
                grade.getId(),
                grade.getLectureName(),
                grade.getYear(),
                grade.getStudentName(),
                grade.getMidterm(),
                grade.getFinalExam(),
                grade.getAssignment(),
                grade.getTotal(),
                grade.getAverage()
            });
        }
    }

    private void addGrade() {
        String selectedLecture = (String) lectureComboBox.getSelectedItem();
        String selectedStudent = (String) studentComboBox.getSelectedItem();
        String midtermText = midtermField.getText().trim();
        String finalExamText = finalExamField.getText().trim();
        String assignmentText = assignmentField.getText().trim();
    
        if (selectedLecture == null || selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "강의와 학생을 선택해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (midtermText.isEmpty() && finalExamText.isEmpty() && assignmentText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "최소 하나의 성적을 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    
        try {
            // 입력값 처리 (빈 입력란은 0으로 처리)
            int midterm = midtermText.isEmpty() ? 0 : Integer.parseInt(midtermText);
            int finalExam = finalExamText.isEmpty() ? 0 : Integer.parseInt(finalExamText);
            int assignment = assignmentText.isEmpty() ? 0 : Integer.parseInt(assignmentText);
        
            // 기존 성적 검색
            Grade existingGrade = grades.stream()
                    .filter(grade -> grade.getLectureName().equals(selectedLecture)
                            && grade.getStudentName().equals(selectedStudent))
                    .findFirst()
                    .orElse(null);
        
            if (existingGrade != null) {
                // 기존 성적 수정
                existingGrade.setMidterm(midterm);
                existingGrade.setFinalExam(finalExam);
                existingGrade.setAssignment(assignment);
                existingGrade.setTotal(midterm + finalExam + assignment);
                existingGrade.setAverage(Math.round((existingGrade.getTotal() / 3.0) * 100) / 100.0);
        
                JOptionPane.showMessageDialog(this, "기존 성적이 수정되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // 새로운 성적 추가
                Student matchedStudent = students.stream()
                        .filter(student -> student.getName().equals(selectedStudent) && student.getLectureName().equals(selectedLecture))
                        .findFirst()
                        .orElse(null);
        
                if (matchedStudent == null) {
                    JOptionPane.showMessageDialog(this, "학생 데이터를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                int id = grades.size() + 1;
                Grade newGrade = new Grade(id, selectedLecture, matchedStudent.getYear(), selectedStudent, midterm, finalExam, assignment);
                grades.add(newGrade);
        
                JOptionPane.showMessageDialog(this, "성적이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            }
        
            // 테이블 갱신
            loadGradesToTable();
        
            // 입력 필드 초기화
            midtermField.setText("");
            finalExamField.setText("");
            assignmentField.setText("");
        
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "성적 입력란에 올바른 숫자를 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
}





class GradeQueryPanel extends JPanel {
    private java.util.List<Grade> grades; // 성적 리스트
    private java.util.List<Lecture> lectures; // 강의 리스트
    private JTable gradeTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> yearComboBox; // 기준년도 필터
    private JComboBox<String> lectureComboBox; // 강의명 필터

    public GradeQueryPanel(java.util.List<Grade> grades, java.util.List<Lecture> lectures) {
        this.grades = grades;
        this.lectures = lectures;
        setLayout(new BorderLayout());

        // 상단 필터 패널 생성
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 기준년도 콤보박스 생성
        filterPanel.add(new JLabel("기준년도:"));
        yearComboBox = new JComboBox<>();
        yearComboBox.addItem("전체"); // 기본값
        yearComboBox.addActionListener(e -> updateLectureComboBox());
        filterPanel.add(yearComboBox);

        // 강의명 콤보박스 생성
        filterPanel.add(new JLabel("강의명:"));
        lectureComboBox = new JComboBox<>();
        lectureComboBox.addItem("전체"); // 기본값
        lectureComboBox.addActionListener(e -> filterGrades());
        filterPanel.add(lectureComboBox);

        add(filterPanel, BorderLayout.NORTH);

        // 테이블 생성
        String[] columns = {"ID", "강의명", "기준년도", "학생명", "중간고사", "기말고사", "과제", "총점", "평균"};
        tableModel = new DefaultTableModel(columns, 0);
        gradeTable = new JTable(tableModel);
        add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        // 초기 데이터 로드
        updateYearComboBox();
    }

    // 기준년도 필터 업데이트
    void updateYearComboBox() {
        yearComboBox.removeAllItems();
        yearComboBox.addItem("전체"); // 기본값

        for (Lecture lecture : lectures) {
            if (!yearComboBoxContains(lecture.getYear())) {
                yearComboBox.addItem(lecture.getYear());
            }
        }

        if (yearComboBox.getItemCount() > 1) {
            yearComboBox.setSelectedIndex(0); // 기본값 선택
        }
    }

    // yearComboBoxContains 메서드 구현
    private boolean yearComboBoxContains(String year) {
        for (int i = 0; i < yearComboBox.getItemCount(); i++) {
            if (yearComboBox.getItemAt(i).equals(year)) {
                return true;
            }
        }
        return false;
    }


    // 강의명 필터 업데이트
    void updateLectureComboBox() {
        lectureComboBox.removeAllItems();
        lectureComboBox.addItem("전체"); // 기본값 추가

        String selectedYear = (String) yearComboBox.getSelectedItem();
        if (selectedYear == null) {
            return; // 기준년도가 선택되지 않은 경우 필터링하지 않음
        }

        for (Lecture lecture : lectures) {
            if (selectedYear.equals("전체") || lecture.getYear().equals(selectedYear)) {
                lectureComboBox.addItem(lecture.getName());
            }
        }

    // 기본값 선택
    if (lectureComboBox.getItemCount() > 0) {
        lectureComboBox.setSelectedIndex(0); // "전체"를 선택
    }

    filterGrades(); // 강의 필터 업데이트 후 성적 필터링 실행
}

    // 성적 데이터 필터링
    private void filterGrades() {
        String selectedYear = (String) yearComboBox.getSelectedItem();
        String selectedLecture = (String) lectureComboBox.getSelectedItem();
    
        // selectedLecture에 대한 null 체크 추가
        if (selectedLecture == null || selectedYear == null) {
            return; // 선택 값이 없으면 필터링하지 않음
        }
    
        tableModel.setRowCount(0);
        for (Grade grade : grades) {
            boolean matchesYear = selectedYear.equals("전체") || grade.getYear().equals(selectedYear);
            boolean matchesLecture = selectedLecture.equals("전체") || grade.getLectureName().equals(selectedLecture);
    
            if (matchesYear && matchesLecture) {
                tableModel.addRow(new Object[]{
                    grade.getId(),
                    grade.getLectureName(),
                    grade.getYear(),
                    grade.getStudentName(),
                    grade.getMidterm(),
                    grade.getFinalExam(),
                    grade.getAssignment(),
                    grade.getTotal(),
                    grade.getAverage()
                });
            }
        }
    }

    // 성적 테이블 갱신
    public void loadGradesToTable() {
        tableModel.setRowCount(0);
        for (Grade grade : grades) {
            tableModel.addRow(new Object[]{
                grade.getId(),
                grade.getLectureName(),
                grade.getYear(),
                grade.getStudentName(),
                grade.getMidterm(),
                grade.getFinalExam(),
                grade.getAssignment(),
                grade.getTotal(),
                grade.getAverage()
            });
        }
    }
}





public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManagementApp();
        });
    }
}