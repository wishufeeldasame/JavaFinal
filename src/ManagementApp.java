import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        // 메뉴바 추가
        createMenuBar();

        // 탭 패널 구성
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("강의 관리", new LecturePanel(lectures));
        tabbedPane.addTab("학생 관리", new StudentPanel());
        tabbedPane.addTab("성적 등록", new GradePanel());
        tabbedPane.addTab("성적 조회", new GradeQueryPanel());

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
        fileMenu.addSeparator(); // 구분선
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
                                                 Integer.parseInt(parts[6]), Integer.parseInt(parts[7]),
                                                 Double.parseDouble(parts[8])));
                            break;
                    }
                }
            }

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

   class LecturePanel extends JPanel {
    private List<Lecture> lectures; // 강의 리스트
    private JTable lectureTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> yearFilterComboBox;
    private JTextField yearField, nameField, timeField;

    public LecturePanel(List<Lecture> lectures) {
        this.lectures = lectures;
        setLayout(new BorderLayout());

        // 상단 패널: 강의 필터링
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("기준년도:"));
        yearFilterComboBox = new JComboBox<>();
        yearFilterComboBox.addItem("전체");
        updateYearFilter();
        yearFilterComboBox.addActionListener(e -> filterLectures());
        topPanel.add(yearFilterComboBox);
        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널: 강의 테이블
        String[] columns = {"등록순번", "기준년도", "강의명", "시간"};
        tableModel = new DefaultTableModel(columns, 0);
        lectureTable = new JTable(tableModel);
        add(new JScrollPane(lectureTable), BorderLayout.CENTER);

        // 하단 패널: 강의 등록 및 삭제
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        // 등록 패널
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registerPanel.add(new JLabel("기준년도:"));
        yearField = new JTextField(5);
        registerPanel.add(yearField);
        registerPanel.add(new JLabel("강의명:"));
        nameField = new JTextField(10);
        registerPanel.add(nameField);
        registerPanel.add(new JLabel("시간:"));
        timeField = new JTextField(5);
        registerPanel.add(timeField);
        JButton addButton = new JButton("등록하기");
        addButton.addActionListener(e -> addLecture());
        registerPanel.add(addButton);
        bottomPanel.add(registerPanel);

        // 삭제 패널
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton deleteAllButton = new JButton("전체 삭제");
        deleteAllButton.addActionListener(e -> deleteAllLectures());
        deletePanel.add(deleteAllButton);
        bottomPanel.add(deletePanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // 초기 데이터 로드
        loadLecturesToTable();
    }

    private void updateYearFilter() {
        yearFilterComboBox.removeAllItems();
        yearFilterComboBox.addItem("전체");
        lectures.stream()
                .map(Lecture::getYear)
                .distinct()
                .forEach(yearFilterComboBox::addItem);
    }

    private void loadLecturesToTable() {
        tableModel.setRowCount(0);
        for (Lecture lecture : lectures) {
            tableModel.addRow(new Object[]{lecture.getId(), lecture.getYear(), lecture.getName(), lecture.getTime()});
        }
    }

    private void filterLectures() {
        String selectedYear = (String) yearFilterComboBox.getSelectedItem();
        tableModel.setRowCount(0);
        for (Lecture lecture : lectures) {
            if ("전체".equals(selectedYear) || lecture.getYear().equals(selectedYear)) {
                tableModel.addRow(new Object[]{lecture.getId(), lecture.getYear(), lecture.getName(), lecture.getTime()});
            }
        }
    }

    private void addLecture() {
        String year = yearField.getText().trim();
        String name = nameField.getText().trim();
        String time = timeField.getText().trim();

        if (year.isEmpty() || name.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 강의명 중복 체크
        for (Lecture lecture : lectures) {
            if (lecture.getName().equals(name)) {
                JOptionPane.showMessageDialog(this, "강의명이 중복됩니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // 새로운 강의 추가
        int id = lectures.size() + 1; // 등록순번
        lectures.add(new Lecture(id, year, name, time));
        JOptionPane.showMessageDialog(this, "강의가 등록되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);

        // 테이블 갱신 및 필터 업데이트
        updateYearFilter();
        filterLectures();

        // 입력 필드 초기화
        yearField.setText("");
        nameField.setText("");
        timeField.setText("");
    }

    private void deleteAllLectures() {
        if (JOptionPane.showConfirmDialog(this, "모든 강의를 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            lectures.clear();
            loadLecturesToTable();
            updateYearFilter();
            JOptionPane.showMessageDialog(this, "모든 강의가 삭제되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

    class StudentPanel extends JPanel{

    }

    class GradePanel extends JPanel{

    }

    class GradeQueryPanel extends JPanel{

    }

}
