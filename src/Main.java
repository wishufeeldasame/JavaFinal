import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;

/*
 * 성적 관리 프로그램의 주요 클래스입니다.
 *
 * @author 서장호
 * @version 1.0 2024.12.24
 * 
 * @created 2024.11.28
 * @updated 2024.12.24
 * 
 */
class ManagementApp extends JFrame {
    private java.util.List<Lecture> lectures = new ArrayList<>();
    private java.util.List<Student> students = new ArrayList<>();
    private java.util.List<Grade> grades = new ArrayList<>();
    private final String FILE_NAME = "data.txt";

    private LecturePanel lecturePanel;
    private StudentPanel studentPanel;
    private GradePanel gradePanel;
    private GradeQueryPanel gradeQueryPanel;

    /*
     * 성적 관리 프로그램의 생성자입니다.
     * 프로그램의 기초 GUI를 구성하고고, 초기 데이터를 로드합니다.
     */
    public ManagementApp() {
        setTitle("성적 관리 프로그램");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 메뉴바 추가
        createMenuBar();

        // 탭 패널 구성
        JTabbedPane tabbedPane = new JTabbedPane();
        lecturePanel = new LecturePanel(lectures, this);
        studentPanel = new StudentPanel(students, this);
        gradePanel = new GradePanel(grades, students, lectures);
        gradeQueryPanel = new GradeQueryPanel(grades,lectures);

        tabbedPane.addTab("강의 관리", lecturePanel);
        tabbedPane.addTab("학생 관리", studentPanel);
        tabbedPane.addTab("성적 등록", gradePanel);
        tabbedPane.addTab("성적 조회", gradeQueryPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // 초기 데이터 로드
        // @throws IOException 파일 로드 중 오류가 발생한 경우
        try {
            loadData();
            gradePanel.updateLectureComboBox();
            gradeQueryPanel.updateYearComboBox();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 로드 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    /*
     * 메뉴바를 생성하고 프레임에 추가합니다.
     * 파일 메뉴와 도움말 메뉴를 구성합니다.
     * 파일 메뉴에는 저장, 불러오기, 종료, 도움말 메뉴에는 About 메뉴를 추가합니다.
     */
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

    /*
     * About 다이얼로그를 표시합니다.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "성적 관리 프로그램 \n" +
                        "제작자: Seojangho\n" +
                        "기능: 강의, 학생, 성적 관리를 위한 프로그램\n" +
                        "100점 주세요",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * 데이터를 저장하는 메소드입니다. 
     * data.txt 파일에
     * #LECTURES, #STUDENTS, #GRADES 섹션으로 데이터를 저장합니다.
     * 
     * @throws IOException 파일 저장 중 오류가 발생한 경우
     * 
     */
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // 강의 저장
            writer.write("#LECTURES\n");
            for (Lecture lecture : lectures) {
                writer.write(lecture.getId() + "|" + lecture.getYear() + "|" + lecture.getName() + "|" + lecture.getTime() + "\n");
            }

            // 학생 저장
            writer.write("#STUDENTS\n");
            for (Student student : students) {
                writer.write(student.getId() + "|" + student.getLectureName() + "|" + student.getYear() + "|" + student.getName() + "|" + student.getStudentId() + "\n");
            }

            // 성적 저장장
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


    /*
     * 데이터를 불러오는 메소드입니다.
     * data.txt 파일에서 데이터를 읽어와서
     * 강의, 학생, 성적 리스트에 저장합니다.
     * 
     * @throws IOException 파일 로드 중 오류가 발생한 경우
     */
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
            //System.out.println("강의 로드 완료: " + lectures.size() + "개");
            //System.out.println("학생 로드 완료: " + students.size() + "개");
            //System.out.println("성적 로드 완료: " + grades.size() + "개");

            lecturePanel.loadLecturesToTable();
            studentPanel.loadStudentsToTable();

            JOptionPane.showMessageDialog(this, "파일 로드가 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 로드 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * loadFromFile() 메소드를 호출하여 데이터를 불러옵니다.
     * 
     */
    private void loadData() throws IOException {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            loadFromFile();
        }
    }

    // Getter 메소드입니다.
    public java.util.List<Lecture> getLectures() {return lectures;}
    public java.util.List<Student> getStudents() {return students;}
    public java.util.List<Grade> getGrades() {return grades;}
    public LecturePanel getLecturePanel() {return lecturePanel;}
    public StudentPanel getStudentPanel() {return studentPanel;}
    public GradePanel getGradePanel() {return gradePanel;}
    public GradeQueryPanel getGradeQueryPanel() {return gradeQueryPanel;}
}



/*
 * 강의 목록 클래스입니다.
 */
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

/*
 * 학생 목록 클래스입니다.
 */
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

/*
 * 성적 클래스입니다.
 */
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

/*
 * 강의 관리 패널 클래스입니다.
 * 강의 목록을 표시하고 강의를 추가하거나 삭제할 수 있습니다.
 * 
 */
class LecturePanel extends JPanel {
    private JTable lectureTable;
    private DefaultTableModel tableModel;
    private java.util.List<Lecture> lectures;
    private ManagementApp app;

    /*
     * 강의 관리 패널의 생성자입니다.
     * 강의 목록과 메인 애플리케이션을 전달받습니다.
     * 
     */
    public LecturePanel(java.util.List<Lecture> lectures, ManagementApp app) {
        this.lectures = lectures;
        this.app = app;
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

        // 버튼 이벤트
        addButton.addActionListener(e -> addLecture());
        deleteButton.addActionListener(e -> deleteLecture());
    }

    /*
     * 강의를 추가하는 메소드입니다.
     * 강의명, 기준년도, 시간을 입력받아 강의를 추가합니다.
     */
    private void addLecture() {
        // JDialog 생성
        JDialog dialog = new JDialog((Frame) null, "강의 추가", true);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간의 여백 설정
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        // 기준년도 필드
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("기준년도:"), gbc);
        JTextField yearField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 0;
        dialog.add(yearField, gbc);
    
        // 강의명 필드
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("강의명:"), gbc);
        JTextField nameField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        dialog.add(nameField, gbc);
    
        // 시간 필드
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("시간 (예: 1교시):"), gbc);
        JTextField timeField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        dialog.add(timeField, gbc);
    
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("추가");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
    
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
    
        // 버튼 동작
        addButton.addActionListener(e -> {
            String year = yearField.getText().trim();
            String name = nameField.getText().trim();
            String time = timeField.getText().trim();
    
            if (year.isEmpty() || name.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "모든 필드를 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                // 강의명 중복 체크
                boolean duplicate = lectures.stream().anyMatch(lecture -> lecture.getName().equals(name));
                if (duplicate) {
                    JOptionPane.showMessageDialog(dialog, "이미 존재하는 강의명입니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                int id = lectures.size() + 1;
                Lecture newLecture = new Lecture(id, year, name, time);
                lectures.add(newLecture);
    
                loadLecturesToTable();
                app.getStudentPanel().loadStudentsToTable();
                app.getGradePanel().updateLectureComboBox();
                app.getGradeQueryPanel().updateYearComboBox();
    
                JOptionPane.showMessageDialog(dialog, "강의가 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });
    
        cancelButton.addActionListener(e -> dialog.dispose());
    
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    /*
     * 강의를 삭제하는 메소드입니다.
     * 테이블에서 선택된 강의를 삭제합니다.
     */
    private void deleteLecture() {
        int selectedRow = lectureTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 강의를 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String lectureName = (String) tableModel.getValueAt(selectedRow, 2);
        lectures.removeIf(lecture -> lecture.getName().equals(lectureName));
        app.getStudents().removeIf(student -> student.getLectureName().equals(lectureName));
        app.getGrades().removeIf(grade -> grade.getLectureName().equals(lectureName));

        loadLecturesToTable();
        app.getGradePanel().updateLectureComboBox();
        app.getGradeQueryPanel().updateYearComboBox();

        JOptionPane.showMessageDialog(this, "강의와 관련된 모든 데이터가 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * 테이블에 강의 목록을 로드하는 메소드입니다.
     * 강의 목록을 테이블에 추가합니다.
     * 
     */
    public void loadLecturesToTable() {
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

/*
 * 학생 관리 패널 클래스입니다.
 * 학생 목록을 표시하고 학생을 추가하거나 삭제할 수 있습니다.
 */
class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private java.util.List<Student> students;
    private ManagementApp app;

    /*
     * 학생 관리 패널의 생성자입니다.
     * 학생 목록과 메인 애플리케이션을 전달받습니다.
     */
    public StudentPanel(java.util.List<Student> students, ManagementApp app) {
        this.students = students;
        this.app = app;
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

        // 버튼 이벤트
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
    }

    /*
     * 학생을 추가하는 메소드입니다.
     * 강의명, 기준년도, 이름, 학번을 입력받아 학생을 추가합니다.
     */
    private void addStudent() {
        // JDialog 생성
        JDialog dialog = new JDialog((Frame) null, "학생 추가", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 간의 여백 설정
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        // 강의명 필드
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("강의명:"), gbc);
        JComboBox<String> lectureComboBox = new JComboBox<>();
        Map<String, String> lectureYearMap = new HashMap<>();
        for (Lecture lecture : app.getLectures()) {
            lectureComboBox.addItem(lecture.getName());
            lectureYearMap.put(lecture.getName(), lecture.getYear());
        }
        gbc.gridx = 1; gbc.gridy = 0;
        dialog.add(lectureComboBox, gbc);
    
        // 기준년도 필드
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("기준년도:"), gbc);
        JTextField yearField = new JTextField();
        yearField.setEditable(false); // 기준년도는 자동으로 설정
        gbc.gridx = 1; gbc.gridy = 1;
        dialog.add(yearField, gbc);
    
        // 강의 선택 시 기준년도 자동 설정
        lectureComboBox.addActionListener(e -> {
            String selectedLecture = (String) lectureComboBox.getSelectedItem();
            if (selectedLecture != null) {
                yearField.setText(lectureYearMap.get(selectedLecture));
            }
        });
    
        if (lectureComboBox.getItemCount() > 0) {
            lectureComboBox.setSelectedIndex(0);
        }
    
        // 학생 이름 필드
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("학생 이름:"), gbc);
        JTextField nameField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        dialog.add(nameField, gbc);
    
        // 학번 필드
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("학번:"), gbc);
        JTextField studentIdField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        dialog.add(studentIdField, gbc);
    
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("추가");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
    
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
    
        // 버튼 동작
        addButton.addActionListener(e -> {
            String lectureName = (String) lectureComboBox.getSelectedItem();
            String year = yearField.getText().trim();
            String name = nameField.getText().trim();
            String studentId = studentIdField.getText().trim();
    
            if (lectureName == null || year.isEmpty() || name.isEmpty() || studentId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "모든 필드를 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                int id = students.size() + 1;
                Student newStudent = new Student(id, lectureName, year, name, studentId);
                students.add(newStudent);
    
                loadStudentsToTable();
                app.getGradePanel().updateStudentComboBox();
                app.getGradeQueryPanel().updateYearComboBox();
    
                JOptionPane.showMessageDialog(dialog, "학생이 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });
    
        cancelButton.addActionListener(e -> dialog.dispose());
    
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    /*
     * 학생을 삭제하는 메소드입니다.
     * 테이블에서 선택된 학생을 삭제합니다.
     */
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 학생을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String studentName = (String) tableModel.getValueAt(selectedRow, 3);
        students.removeIf(student -> student.getName().equals(studentName));
        app.getGrades().removeIf(grade -> grade.getStudentName().equals(studentName));

        loadStudentsToTable();
        app.getGradePanel().updateStudentComboBox();
        app.getGradeQueryPanel().updateYearComboBox();

        JOptionPane.showMessageDialog(this, "학생과 관련된 성적 데이터가 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * 테이블에 학생 목록을 로드하는 메소드입니다.
     * 학생 목록을 테이블에 추가합니다.
     */
    public void loadStudentsToTable() {
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

/*
 * 성적 관리 패널 클래스입니다.
 * 성적 목록을 표시하고 성적을 추가할 수 있습니다.
 */
class GradePanel extends JPanel {
    private java.util.List<Grade> grades;
    private java.util.List<Student> students;
    private java.util.List<Lecture> lectures;
    private JTable gradeTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> lectureComboBox;
    private JComboBox<String> studentComboBox;
    private JTextField midtermField, finalExamField, assignmentField;

    /*
     * 성적 관리 패널의 생성자입니다.
     * 성적 목록, 학생 목록, 강의 목록을 전달받습니다.
     */
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

    /*
     * 강의 콤보박스를 업데이트하는 메소드입니다.
     * 강의 목록을 콤보박스에 추가합니다.
     */
    void updateLectureComboBox() {
        lectureComboBox.removeAllItems();
        for (Lecture lecture : lectures) {
            lectureComboBox.addItem(lecture.getName());
        }

        if (lectureComboBox.getItemCount() > 0) {
            lectureComboBox.setSelectedIndex(0);
            updateStudentComboBox();
        } else {
            //System.out.println("강의 목록이 비어 있습니다.");
        }
    }

    /*
     * 학생 콤보박스를 업데이트하는 메소드입니다.
     * 선택된 강의에 등록된 학생을 콤보박스에 추가합니다.
     */
    public void updateStudentComboBox() {
        studentComboBox.removeAllItems();
        String selectedLecture = (String) lectureComboBox.getSelectedItem();
        //System.out.println("선택된 강의: " + selectedLecture);

        if (selectedLecture != null) {
            for (Student student : students) {
                if (student.getLectureName().equals(selectedLecture)) {
                    studentComboBox.addItem(student.getName());
                }
            }
        }

        if (studentComboBox.getItemCount() == 0) {
            //System.out.println("선택된 강의에 등록된 학생이 없습니다.");
        }
    }

    /*
     * 성적을 테이블에 로드하는 메소드입니다.
     * 성적 목록을 테이블에 추가합니다.
     */
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

    /*
     * 성적을 추가하는 메소드입니다.
     * 입력된 성적을 성적 목록에 추가합니다.
     */
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

/*
 * 성적 조회 패널 클래스입니다.
 * 성적 목록을 필터링하여 표시할 수 있습니다.
 */
class GradeQueryPanel extends JPanel {
    private java.util.List<Grade> grades; // 성적 리스트
    private java.util.List<Lecture> lectures; // 강의 리스트
    private JTable gradeTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> yearComboBox; // 기준년도 필터
    private JComboBox<String> lectureComboBox; // 강의명 필터

    /*
     * 성적 조회 패널의 생성자입니다.
     * 성적 목록과 강의 목록을 전달받습니다.
     */
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

    /*
     * 기준년도 콤보박스 업데이트
     * 기준년도 목록을 콤보박스에 추가합니다.
     */
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

    /*
     * yearComboBox에 year가 포함되어 있는지 확인하는 메소드입니다.
     */
    private boolean yearComboBoxContains(String year) {
        for (int i = 0; i < yearComboBox.getItemCount(); i++) {
            if (yearComboBox.getItemAt(i).equals(year)) {
                return true;
            }
        }
        return false;
    }


    /*
     * 강의명 콤보박스 업데이트
     * 선택된 기준년도에 해당하는 강의명을 콤보박스에 추가합니다.
     */
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

    /*
     * 성적 필터링 메소드입니다.
     * 기준년도와 강의명에 따라 성적을 필터링합니다.
     */
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

    /*
     * 테이블에 성적 목록을 로드하는 메소드입니다.
     * 성적 목록을 테이블에 추가합니다. 
     */
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