export interface ReleaseRequest {
  id?: string;
  fecha?: string;
  equipo: string;
  tipo: string;
  descripcion: string;
  prId?: string;
  cobertura?: number;
  stack?: string;
  notificationEmail?: string;
  estado?: string;
  tipoAprobacion?: string;
  razonesRechazo?: string[];
}
