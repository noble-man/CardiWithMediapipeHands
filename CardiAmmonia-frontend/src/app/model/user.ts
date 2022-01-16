import { Role } from './role';

export interface User {
	userId: number;
	password: string;
    username: string;
	role: Role;
	enabled: boolean;
}
