class Grade {
    private int id;
    private String lectureName;
    private String year;
    private String studentName;
    private int midterm, finalExam, assignment, total;
    private double average;

    public Grade(int id, String lectureName, String year, String studentName, int midterm, int finalExam, int assignment, int total, double average) {
        this.id = id;
        this.lectureName = lectureName;
        this.year = year;
        this.studentName = studentName;
        this.midterm = midterm;
        this.finalExam = finalExam;
        this.assignment = assignment;
        this.total = total;
        this.average = average;
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