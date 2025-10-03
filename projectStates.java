import java.util.ArrayList;

//interface
interface ProjectState {
    void forward(Project context);
    void getStatus();
    String draftReport();
    void raiseRisk(String risk);
    void assignTasks(String department, String employeeID, Task task);
} 

// Dummy Task class so assignTasks compiles
class Task {
    private String name;
    public Task(String name) { this.name = name; }
    public String getName() { return name; }
}

// ===================== DraftState =====================
class DraftState implements ProjectState {
    private ArrayList<String> deadlines;
    private ArrayList<String> departmentsInvolved;
    private ArrayList<String> clientRequirements;
    private String projectType;

    public DraftState(ArrayList<String> deadlines, ArrayList<String> departmentsInvolved, ArrayList<String> clientRequirements, String projectType) {
        this.deadlines = deadlines;
        this.departmentsInvolved = departmentsInvolved;
        this.clientRequirements = clientRequirements;
        this.projectType = projectType;
    }

    @Override
    public void forward(Project context) {
        System.out.println("Submitting for approval...");
        String report = draftReport();
        context.setState(new ActiveState(this.projectType, report));
    }

    @Override
    public void getStatus() {
        System.out.println("Project is in Draft.");
        draftReport();
    }

    @Override
    public void raiseRisk(String risk) {
        System.out.println("Cannot raise risk in Draft state");
    }

    @Override
    public void assignTasks(String department, String employeeID, Task task) {
        System.out.println("Cannot assign tasks in Draft state");
    }

    @Override
    public String draftReport() {
        StringBuilder sb = new StringBuilder("Project Intake Review\n");
        sb.append("\nDeadlines\n");
        sb.append(Project.printStringArray(deadlines));
        sb.append("\nDepartments Involved\n");
        sb.append(Project.printStringArray(departmentsInvolved));
        sb.append("\nClient Requirements\n");
        sb.append(Project.printStringArray(clientRequirements));
        System.out.printf("Report: \n%s\n", sb.toString());
        return sb.toString();
    }
}

// ===================== ActiveState =====================
class ActiveState implements ProjectState {
    private ArrayList<String> tasks = new ArrayList<>();
    private String projectType;
    private String draftReport;

    enum Action { AtRisk, OnHold, Submitted }
    private Action currentAction = Action.Submitted;

    private ArrayList<String> milestones = new ArrayList<>();
    private ArrayList<String> risks = new ArrayList<>();

    public ActiveState(String projectType, String draftReport) {
        this.projectType = projectType;
        this.draftReport = draftReport;
    }

    @Override
    public void raiseRisk(String risk) {
        risks.add(risk);
        System.out.println("Risk added: " + risk);
    }

    @Override
    public String draftReport() {
        System.out.println("Cannot draft new report in Active state.");
        return draftReport;
    }

    @Override
    public void forward(Project context) {
        if (currentAction == Action.Submitted) {
            context.setState(new SubmitState());
        } else if (currentAction == Action.AtRisk) {
            context.setState(new AtRiskState());
        } else if (currentAction == Action.OnHold) {
            context.setState(new OnHoldState());
        }
    }

    @Override
    public void getStatus() {
        System.out.println("Project is in Active State.");
    }

    @Override
    public void assignTasks(String department, String employeeID, Task task) {
        tasks.add(task.getName() + " -> " + department + ":" + employeeID);
        System.out.println("Assigned task: " + task.getName() + " to " + employeeID + " in " + department);
    }
}

// ===================== SubmitState =====================
class SubmitState implements ProjectState {
    @Override
    public void forward(Project context) {
        System.out.println("Project approved and moving forward.");
        context.setState(new ActiveState("Default", "Auto-generated report"));
    }

    @Override
    public void getStatus() {
        System.out.println("Project is in Submitted state, awaiting approval.");
    }

    @Override
    public String draftReport() {
        return "Cannot draft report in Submitted state.";
    }

    @Override
    public void raiseRisk(String risk) {
        System.out.println("Cannot raise risk while waiting for approval.");
    }

    @Override
    public void assignTasks(String department, String employeeID, Task task) {
        System.out.println("Cannot assign tasks in Submitted state.");
    }
}

// ===================== AtRiskState =====================
class AtRiskState implements ProjectState {
    private ArrayList<String> risks = new ArrayList<>();

    @Override
    public void forward(Project context) {
        System.out.println("Attempting to resolve risks...");
        if (risks.isEmpty()) {
            System.out.println("No risks left, moving to Active state.");
            context.setState(new ActiveState("Default", "Recovered report"));
        } else {
            System.out.println("Risks still pending, staying in AtRisk state.");
        }
    }

    @Override
    public void getStatus() {
        System.out.println("Project is At Risk!");
    }

    @Override
    public String draftReport() {
        return "Cannot draft report in AtRisk state.";
    }

    @Override
    public void raiseRisk(String risk) {
        risks.add(risk);
        System.out.println("Risk added in AtRisk state: " + risk);
    }

    @Override
    public void assignTasks(String department, String employeeID, Task task) {
        System.out.println("Tasks paused while project is at risk.");
    }
}

// ===================== OnHoldState =====================
class OnHoldState implements ProjectState {
    @Override
    public void forward(Project context) {
        System.out.println("Resuming project from OnHold...");
        context.setState(new ActiveState("Default", "Resumed report"));
    }

    @Override
    public void getStatus() {
        System.out.println("Project is currently On Hold.");
    }

    @Override
    public String draftReport() {
        return "Cannot draft report in OnHold state.";
    }

    @Override
    public void raiseRisk(String risk) {
        System.out.println("Cannot raise risk while project is On Hold.");
    }

    @Override
    public void assignTasks(String department, String employeeID, Task task) {
        System.out.println("Cannot assign tasks while On Hold.");
    }
}

// ===================== Project Context =====================
class Project {
    private ProjectState state;

    public Project(ArrayList<String> deadlines, ArrayList<String> departmentsInvolved, 
    ArrayList<String> clientRequirements, String projectType) {
        this.state = new DraftState(deadlines, departmentsInvolved, clientRequirements, projectType);
    }

    public void setState(ProjectState state) {
        this.state = state;
    }

    public void forward() {
        state.forward(this);
    }

    public void getStatus() {
        state.getStatus();
    }

    public String draftReport() {
        return state.draftReport();
    }

    public void raiseRisk(String risk) {
        state.raiseRisk(risk);
    }

    public void assignTasks(String department, String employeeID, Task task) {
        state.assignTasks(department, employeeID, task);
    }

    public static String printStringArray(ArrayList<String> toPrint) {
        StringBuilder sb = new StringBuilder();
        for (String item : toPrint) {
            sb.append(item).append("\n");
        }
        return sb.toString();
    }
}
// ===================== Main =====================
class ProjectStates {
    public static void main(String[] args) {
        ArrayList<String> deadlines = new ArrayList<>();
        deadlines.add("2025-10-10");
        ArrayList<String> departments = new ArrayList<>();
        departments.add("Engineering");
        ArrayList<String> requirements = new ArrayList<>();
        requirements.add("Security compliance");

        Project project = new Project(deadlines, departments, requirements, "IT Upgrade");

        project.getStatus();    // Draft
        project.forward();      // Submit -> Active
        project.getStatus();    // Active
        project.raiseRisk("Budget overrun");
        project.forward();      // Moves to AtRisk
        project.getStatus();    // AtRisk
    }
}