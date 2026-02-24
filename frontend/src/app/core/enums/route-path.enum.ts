export enum RoutePath {
  Login = 'login',
  List = '',
  Novo = 'novo',
  Editar = 'editar',
  Transferir = 'transferir',
}

export function routeEditar(id: number): string {
  return `${RoutePath.Editar}/${id}`;
}
