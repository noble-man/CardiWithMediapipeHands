package be.lilab.uclouvain.cardiammonia.application.production;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class Production {

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String productionId;
	
    @CreationTimestamp
    private Timestamp startedAt;
    private Timestamp finishedAt;
    private String statusId;//STS_STARTED,STS_PAUSED, STS_REJECTED, STS_COMPLETED
    private String userId;//The user who created the production
    @OneToMany(mappedBy="production")
    private Collection<ProductionLog> logs;
    private String recipeId;
	private String route; //0: waste, 1: syringe, 2: qc
	private Double actualActivity = 0d;
    public Production() {
    	logs = new HashSet<>();
    	
    }
	public Production(String productionId, /*Timestamp startedAt, Timestamp finishedAt, String statusId,*/ String recipeId, String userId) {
		this(productionId, null, null, "STS_STARTED", recipeId, "1", userId, new HashSet<>());
	}    
	public Production(String productionId, Timestamp startedAt, Timestamp finishedAt, String statusId, String recipeId, String route, String userId,
			Set<ProductionLog> logs) {
		super();
		this.productionId = productionId;
		this.startedAt = startedAt;
		this.finishedAt = finishedAt;
		this.statusId = statusId;
		this.userId = userId;
		this.logs = logs;
		this.recipeId = recipeId;
		this.route = route;
	}
	public String getProductionId() {
		return productionId;
	}
	public void setProductionId(String productionId) {
		this.productionId = productionId;
	}
	public Timestamp getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(Timestamp startedAt) {
		this.startedAt = startedAt;
	}
	public Timestamp getFinishedAt() {
		return finishedAt;
	}
	public void setFinishedAt(Timestamp finishedAt) {
		this.finishedAt = finishedAt;
	}
	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Collection<ProductionLog> getLogs() {
		return logs;
	}
	public void setLogs(Set<ProductionLog> logs) {
		this.logs = logs;
	}
	public String getRecipeId() {
		return recipeId;
	}
	public void setRecipeId(String recipetId) {
		this.recipeId = recipetId;
	}
	public String getRoute() {
		return this.route;
	}
	public void setRouteId(String route) {
		this.route = route;
	}
	public void setActualActivity(Double actualActivity) {
		this.actualActivity = actualActivity;
	}
	public Double getActualActivity() {
		return actualActivity;
	}
}
