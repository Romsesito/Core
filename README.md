# Backend Development: Plataforma de Comisiones de Arte

#MEJORAS DE PATRONES DE DISEÑO CORE - NUEVA ASIGNACION:

VIDEO: https://www.youtube.com/watch?v=Jz-wHDM7fNU 
DEPLOY: https://frontcoreartist.onrender.com 

## 1. Resumen del Proyecto

Este documento resume el desarrollo del backend para una plataforma de comisiones de arte. El sistema permite a clientes solicitar servicios de arte, a artistas ofrecer sus habilidades y tomar pedidos, y a un dueño de la plataforma gestionar el catálogo de habilidades, aprobar pedidos y facilitar la asignación de artistas a los trabajos. Una característica central es un motor de decisión para ayudar al dueño a asignar artistas comparando los requisitos del pedido con las habilidades y carga de trabajo de los artistas.

DEPLOY https://frontcoreartist.onrender.com 

## 2. Tecnologías Utilizadas

* **Lenguaje:** Java (JDK 21 TEMURIN)
* **Framework:** Spring Boot 3.x
* **Gestor de Dependencias y Build:** Gradle 
* **Persistencia de Datos:** Spring Data JPA (con Hibernate)
* **Mapeo de Objetos:** ModelMapper
* **Utilidades:** Lombok
* **Servidor Web Embebido:** Tomcat 

## 3. Video Explicativo del Funcionamiento

La explicación detallada y demostración del funcionamiento de la aplicación se encuentra en el siguiente video:

[**VER VIDEO EXPLICATIVO AQUÍ**](https://www.youtube.com/watch?v=kB6GZlV-tFg)


## 4. Seguimiento de Tareas en Jira (Capturas de Pantalla)

A continuación, se adjuntan capturas de pantalla del tablero de Jira utilizado para la gestión y seguimiento de las tareas de desarrollo de este backend.

![{BE91F648-48E1-4663-AA13-2E44C66A22C2}](https://github.com/user-attachments/assets/1d7d1f8b-bab4-4005-864b-da818998769e)
![{243F46C8-8950-48BB-923D-E2F964C6E888}](https://github.com/user-attachments/assets/cbafa4ea-d7c3-4b51-a0f1-566f3651ab37)


---

## 5. SCREENSHOOTS
El sistema permite recibir comisiones! Cuando un cliente hace un pedido, especifica las habilidades que busca. El sistema compara estas habilidades con las de los artistas usando dos tablas para encontrar la mejor coincidencia posible

![{D281E28A-5D50-4064-889F-D4CB3B6493D5}](https://github.com/user-attachments/assets/ec68e3ca-850a-4bcd-9759-329f67cd50a9)
![{363AE8D6-6EB8-4724-A710-68F0A61614BF}](https://github.com/user-attachments/assets/97be6411-664c-4732-9497-50129e3996bd)
![{46C69EB2-6B11-430C-9193-9FFBFB0698EA}](https://github.com/user-attachments/assets/7e1dd18d-21ab-41f4-84d5-4181a6467bf7)
![{90AF6005-C45F-4388-BCDB-8858BFA2948E}](https://github.com/user-attachments/assets/76680549-b0fe-4925-b928-cbdb0040f9f2)


