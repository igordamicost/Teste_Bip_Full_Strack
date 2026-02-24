# Frontend Angular (Benefícios)

App Angular com Material, autenticação JWT e CRUD de benefícios.

## Testes (Jest, Karma e Jasmine)

Os mesmos arquivos `*.spec.ts` rodam com **Jasmine** em dois runners:

| Comando | Runner | Uso |
|---------|--------|-----|
| `npm test` ou `npm run test:karma` | **Karma** + Jasmine | Testes no browser (modo watch); abre UI em http://localhost:9876 |
| `npm run test:ci` | **Karma** + Jasmine | CI: uma execução, ChromeHeadless |
| `npm run test:jest` | **Jest** + Jasmine API | Testes em Node (rápido, sem browser) |

- **Jasmine**: framework de testes (sintaxe `describe`, `it`, `expect`, `jasmine.createSpyObj`).
- **Karma**: executa os testes em um browser real (Chrome por padrão).
- **Jest**: executa os testes em Node com `jest-preset-angular`; cobertura em `coverage/jest/`.

### Desenvolvimento

```bash
npm install
npm start 
npm test  
npm run test:jest
```