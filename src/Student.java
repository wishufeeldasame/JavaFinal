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