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