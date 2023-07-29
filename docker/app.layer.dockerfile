FROM gradle:7.6.1-jdk17

WORKDIR /home/foodbowl

RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

COPY ../dependencies .
COPY ../spring-boot-loader .
COPY ../snapshot-dependencies .
COPY ../application .

ARG SPRING_PROFILE=dev

ENTRYPOINT [ \
    "java", \
    "-Dspring.profiles.active=${SPRING_PROFILE}", \
    "-Duser.timezone=Asia/Seoul", \
    "org.springframework.boot.loader.JarLauncher" \
]
