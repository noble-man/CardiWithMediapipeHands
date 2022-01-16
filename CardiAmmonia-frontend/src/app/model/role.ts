import { Permission } from './permission';

export interface Role {
	roleId: string;
	description: string;
	permissions: Permission[];
}
