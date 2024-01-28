export interface Questao {
    id: number;
    enunciado: string;
    tipo: string;
    opcoes?: string[];
    respostaCorreta: string;
  }