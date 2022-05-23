import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// This is the main class where the operation is performed. 
// It has two methods which triggers all the task
class ProjectManagement {
	private List<Task> totalTask = new ArrayList<>();
	
	public Duration triggerAllTask(List<Task> allTask) throws Exception {
		Long startTime = new Date().getTime();
		List<Task> orderedTask = calculatedTaskGraph(allTask);
	    for (Task task : orderedTask) {
	    	task.performTask(task.getDependentTask(), task.getPeopleHour(), task.getResourceList());
		}
		return Duration.ofMillis(new Date().getTime()-startTime);
	}

	public List<Task> calculatedTaskGraph(List<Task> allTask) {
		// TODO Auto-generated method stub
		
		// implement this method to find the dependency graph and get the ordered list of graph. Throw exception if there is any cycle dependency
		return null;
	}

	public ProjectManagement(List<Task> totalTask) {
		super();
		this.totalTask = totalTask;
	}
	
}


class Task{
	private List<Task> dependentTask = new ArrayList<Task>();
	private List<UserTime> peopleHour = new ArrayList<UserTime>();
	private List<Resource> resourceList;
	private TaskStatus taskstatus;
	
	
	public List<Task> getDependentTask() {
		return dependentTask;
	}

	public void setDependentTask(List<Task> dependentTask) {
		this.dependentTask = dependentTask;
	}

	public List<UserTime> getPeopleHour() {
		return peopleHour;
	}

	public void setPeopleHour(List<UserTime> peopleHour) {
		this.peopleHour = peopleHour;
	}

	public List<Resource> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<Resource> resourceList) {
		this.resourceList = resourceList;
	}

	public TaskStatus getTaskstatus() {
		return taskstatus;
	}

	public void setTaskstatus(TaskStatus taskstatus) {
		this.taskstatus = taskstatus;
	}

	public Task(List<Task> dependentTask, List<UserTime> peopleHour, List<ResourceRequired> resourceNeed) {
		super();
		this.dependentTask = dependentTask;
		this.peopleHour = peopleHour;
		this.resourceList = InventoryManagement.fetchAllResource(resourceNeed);
	}

// This Method checks whether dependent Task completed. If yes , it will peform the user action based on time required. It will return the total required to complete the task
	public Duration performTask(List<Task> dependentTask, List<UserTime> peopleHour,List<Resource> resourceNeed ) throws Exception {
		this.taskstatus = TaskStatus.STARTED;
		Long startTime = new Date().getTime();
		if(checkDependentTaskCompleted(dependentTask)) {
			peopleHour.forEach(e -> {
				try {
					Thread.sleep(e.getTimeRequired().getSeconds());
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//Add logger
				}
			});
			 Duration timeTakenToCompleteTask = Duration.ofMillis(new Date().getTime() - startTime);
			 return timeTakenToCompleteTask;
		}else {
			throw new Exception("depended Task not completed");
		}
    this.taskstatus = TaskStatus.COMPLETED; // We can use running status too based on our need. 
	}


	private boolean checkDependentTaskCompleted(List<Task> dependentTask) {
		// TODO Auto-generated method stub
		//Add method to check the status of the each task
		return false;
	}
	
}

class ResourceRequired{
	private Resource resource;
	private int count;
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}

class UserTime {
	private User user;
	private Duration timeRequired;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Duration getTimeRequired() {
		return timeRequired;
	}
	public void setTimeRequired(Duration timeRequired) {
		this.timeRequired = timeRequired;
	}
}

class User {
	private int userId;
	private String userName;
	private Skill skill;
}

class Skill {
	private int skillId;
	private String skillName;
	private SkillProficiency skillProficiency;
}

enum SkillProficiency {
	BEGINNER, INTERMEDIATE, ADVANCED
}

enum TaskStatus {
	PENDING, STARTED, RUNNING,COMPLETED
}
class InventoryManagement {
	private static Map<Integer, Resource> resourceMap = new ConcurrentHashMap<Integer, Resource>();
	// This method add new Resource to inventory
	public static void addNewResource(int resourceId, Resource resource) {
		resourceMap.put(resourceId, resource);
	}

    //This method add resource count to existing resource
	public static Resource addResourceCount(int resourceId, int count) {
		Resource resource = resourceMap.get(resourceId);
		AtomicInteger availableCount = resource.getAvailableCount();
		availableCount.addAndGet(count);
		resource.setAvailableCount(availableCount);
		return resourceMap.put(resourceId, resource);
	}

	 //This method fetch resource and decrease its count from existing resource
	public static Resource fetchResourceCount(int resourceId, int count) {
		Resource resource = resourceMap.get(resourceId);
		AtomicInteger availableCount = resource.getAvailableCount();
		int newValue = availableCount.get() - count;
		if (newValue >= 0) {
			availableCount.set(newValue);
			resource.setAvailableCount(availableCount);
		} else {
			System.out.println("Resource not avialable"); // we need to throw exception and handle it. I have given
															// sysout for making it simple
		}
		return resourceMap.put(resourceId, resource);
	}
	
	//This method fetch all resource and decrease its count from existing resource
	public static List<Resource> fetchAllResource(List<ResourceRequired> requestList){
		return requestList.stream().map(e -> fetchResourceCount(e.getResource().getResourceId(),e.getCount())).collect(Collectors.toList());
	}
	
}

class Resource {
	private int resourceId;
	private String resourceName;
	private AtomicInteger availableCount; // Count which is available to user
	private AtomicInteger totalCount;

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public AtomicInteger getAvailableCount() {
		return availableCount;
	}

	public void setAvailableCount(AtomicInteger availableCount) {
		this.availableCount = availableCount;
	}

	public AtomicInteger getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(AtomicInteger totalCount) {
		this.totalCount = totalCount;
	}

}
