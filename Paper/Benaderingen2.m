clear all
clc

%%

Xas = (1:5)';
Yas =  [  25331106
          61339344
          245313463
          5804849367
          12366987991024
       ];

figure
plot(Xas, Yas, '*-')
set(gca, 'Yscale', 'log')
hold on
newX = 1:7
plot(newX, 100000*factorial(newX).^4)

(100000*factorial(6).^4) / (1000000000 * 60 * 60 * 24)

%%

Xas = (1:3)';
Yas =  [  
          245313463
          5804849367
          12366987991024
       ];

figure
plot(Xas, Yas, '*-')
set(gca, 'Yscale', 'log')
hold on
newX = 1:5
plot(newX, 100000000*factorial(newX).^7)

(100000*factorial(6).^4) / (1000000000 * 60 * 60 * 24)