# Használj egy hivatalos Java runtime alap képet
FROM openjdk:17-jdk-slim

# Add meg az alkalmazás portját, amit a konténernek ki kell tennie
EXPOSE 8080

# Másold a war fájlt az alkalmazás gyökérkönyvtárába a konténerben
COPY ./target/baccaratio-1.0.0.war app.war

# Futtasd az alkalmazást
ENTRYPOINT ["java", "-jar", "/app.war"]
