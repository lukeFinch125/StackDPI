import java.util.ArrayList;

//interface
interface ProjectState {
    void forward(Project context);
    void getStatus();
}

//concrete states
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

    private String draftReport() {
        StringBuilder sb = new StringBuilder("Project Intake Review\n");
        sb.append("\nDeadlines\n");
        sb.append(context.printStringArray(deadlines));
        sb.append("\nDepartments Involved\n");
        sb.append(context.printStringArray(departmentsInvolved));
        sb.append("\nClient Requirements\n");
        sb.append(context.printStringArray(clientRequirements));
        System.out.print("Report: \n %s", sb);
        return sb.toString();
    }
}

class ActiveState implements ProjectState {
    private ArrayList<String> tasks;
    private String projectType;
    private String draftReport;
    private enum action {

    }

    public ActiveState(String projectType, String draftReport) {
        this.projectType = projectType;
        this.draftReport = draftReport;
    }

    @Override
    public void forward(Project context) {
        if(action == approved) {
            System.out.println("Message approved");
        }
    }
}

//context
class Project {
    private ProjectState state;

    public Project(ArrayList<String> deadlines, ArrayList<String> departmentsInvolved, ArrayList<String> clientRequirements) {
        this.state = new DraftState(deadLines, departmentsInvolved, clientRequirements);
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

    public String printStringArray(ArrayList<String> toPrint) {
        String toReturn = "";
        for(int i = 0; i < toPrint.length(); i++) {
            toReturn += toPrint[i] + "\n";
        }
        return toReturn;
    }
}

class projectStates {
    public static void main(String args[]) {
        System.out.println("hello");
    }
}