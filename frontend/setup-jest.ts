/// <reference types="node" />
/// <reference types="jest" />
// Setup para Jest (jest-preset-angular) + compatibilidade com Jasmine
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

// Polyfill para specs que usam jasmine.createSpyObj no Jest
const g = typeof globalThis !== 'undefined' ? globalThis : (typeof global !== 'undefined' ? global : (typeof window !== 'undefined' ? window : {}));
(g as Record<string, unknown>)['jasmine'] = {
  createSpyObj: (_name: string, methods: string[]) => {
    const o: Record<string, ReturnType<typeof jest.fn> & { and: { returnValue: (v: unknown) => void } }> = {};
    methods.forEach((m) => {
      const fn = jest.fn();
      (fn as unknown as { and: { returnValue: (v: unknown) => void } }).and = {
        returnValue: (v: unknown) => fn.mockReturnValue(v),
      };
      o[m] = fn as ReturnType<typeof jest.fn> & { and: { returnValue: (v: unknown) => void } };
    });
    return o;
  },
};
