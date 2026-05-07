export interface AuthSession {
  nombre: string;
  email: string;
  role: 'USER' | 'GUEST';
}
