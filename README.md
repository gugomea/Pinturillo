# Pinturillo

Juego en el que hay una sala con un pintor y el resto intenta adivinar el dibujo. El rol de pintor se va rotando.
Los mensajes son mandados por TCP, debido a que si el ratón se mueve rápido los píxeles quedan separados se calcula en cada
cliente ese error, para no inundar la red de paquetes(ya que hemos reducido la cantidad de paquetes podemos usar TCP).

El servidor se conecta a una página web que tiene una tabla con 100 sustantivos y parsea el html para guardar estos sustantivos
en una estructura del tipo String[].

#Ejemplo de uso


https://github.com/gugomea/Pinturillo/assets/91557704/3393d31b-4789-482a-bc02-a36af2699066

