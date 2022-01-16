package be.lilab.uclouvain.cardiammonia.application.production;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class ProductionLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int productionLogId;
	private String machineName;
	private String stateName;
	private String parameterName;
	private String parameterValue;
    @CreationTimestamp
    private Timestamp startedAt;

    @ManyToOne
    @JoinColumn(name="productionId", nullable=false)
    private Production production;

    public ProductionLog() {
    	
    }
	public ProductionLog(String machineName, String stateName, String parameterName,
			String parameterValue, Production production) {
		super();
		this.productionLogId = productionLogId;
		this.machineName = machineName;
		this.stateName = stateName;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.startedAt = startedAt;
		this.production = production;
	}

	public int getProductionLogId() {
		return productionLogId;
	}

	public void setProductionLogId(int productionLogId) {
		this.productionLogId = productionLogId;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public Timestamp getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Timestamp startedAt) {
		this.startedAt = startedAt;
	}

	public Production getProduction() {
		return production;
	}

	public void setProduction(Production production) {
		this.production = production;
	}

    
	
}
