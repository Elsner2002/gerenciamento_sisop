# Trabalho 2 de Sistemas Operacionais

Bernardo Barzoto Zomer, Felipe Elsner da Silva, Lucas Marchesan Cunha

Professor Fernando Dotti

## Seção implementação

### T2a

Para implementar a perda do processador por tempo de uso, usamos um sistema
round-robin, onde, após executar por um determinado número de ciclos, o
processo libera o processador para que outro possa usá-lo e assim por diante.

Para isso, foi implementado um sistema de quantum na CPU e o processo run foi
atualizado, de forma que ele impede o processo de rodar caso ele tenha
completado seu ciclo de quantum. Nesse caso, ele ativa a interrupção de
Timeout, que por sua vez marca o programa mais uma vez como pronto e o insere
novamente na fila.

Dessa forma, ao terminar de executar um processo por falta
de tempo, ele retorna ao final da fila de execução conforme o próximo
processo da fila é chamado e assim por diante, até que ele chegue ao final
de sua execução, demarcada pelo instrução `STOP`, que ao ser executado mata
o processo.

### T2b

Para implementar concorrência no sistema o código foi reorganizado para
fazer uso de múltiplas threads em sua execução. Essas threads foram criadas
usando a biblioteca padrão do Java, a qual sobrescreve o processo "run" de um
programa para opera-lo como uma thread. Com isso, foram criadas 3 threads que
rodam constantemente, cada uma com sua utilidade específica no código.

A primeira delas, a thread Shell, é responsável por ler os inputs do
usuário, ou seja, ler os comandos relacionados a criação e execução de
processos, além de comandos relacionados a memória. A segunda thread, CPU,
é responsável por executar os programas fornecidos. Por fim, a terceira
thread, Interrupt Handler, é responsável por lidar com todos os comandos de
I/O, que previamente eram executados na CPU.

Para isso, uma vez que a CPU lê uma instrução de I/O, esse processo entra
no estado de bloqueado e não é mais executado pela CPU. Em vez disso, ele
entra em uma fila de processos a serem tratados pela thread Interrupt Handler,
a qual irá executar o processo de I/O, fazendo com que ele não atrase a
execução da CPU. Por fim, quando o I/O for tratado, o estado do processo é
alterado novamente para ready e ele volta para a final de programas a serem
executados pela CPU.

## Seção de testes

Para executar o código, compile todos os `*.java` e execute `java
OperatingSystem`.

### Programas

- `fac` para o programa `factorial`;
- `factrap` para o programa `factorialTrap`;
- `min` para o programa `minimalProgram`;
- `fib` para o programa `fibonacci`;
- `fibtrap` para o programa `fibonacciTrap`;
- `pb` para o programa `pb`.

### Comandos

- `new <nomeDePrograma>` equivale ao comando `cria <nomeDePrograma>`;
- `ps` equivale ao comando `listaProcessos`;
- `pdump <id>` equivale ao comando `dump <id>`;
- `kill <id>` equivale ao comando `desloca <id>`;
- `mdump <inicio,fim>` equivale ao comando `dumpM <inicio,fim>`;
- `trace` equivale aos comandos `traceOn` e `traceOff`, invertendo o valor atual
  da variável trace;
- `exit` equivale ao comando `exit`;
- `slow` faz a CPU esperar um segundo entre cada ciclo e serve apenas para
  depuração.

### Roteiro de teste

 1. `slow`
 2. `new fac`
 3. `new factrap`
 4. `mdump 0 2`
 5. `new fib`
 6. `new fibtrap`
 7. `ps`
 8. `10`
 9. `mdump 6 7`
10. `new factrap`
11. `new fibtrap`
12. `ps`
13. `11`
14. `mdump 0 7`
15. `Ctrl + C`
