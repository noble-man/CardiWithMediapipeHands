import { ProductionLog } from './production-log';

export interface Production {
	productionId:string;
	
    startedAt: string;
    finishedAt: string;
    statusId:string;//STS_STARTED,STS_PAUSED, STS_REJECTED, STS_COMPLETED
    userId:string;//The user who created the production
    logs:ProductionLog[];
    recipeId:string;
}
