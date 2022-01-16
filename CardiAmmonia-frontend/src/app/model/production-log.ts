export interface ProductionLog {
	productionLogId:number;
	machineName:string;
	stateName:string;
	parameterName:string;
	parameterValue:string;
    startedAt:string;
	production:any
}
