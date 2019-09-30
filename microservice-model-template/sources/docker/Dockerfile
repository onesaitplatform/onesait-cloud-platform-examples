FROM maven:3.6-jdk-8
#FROM maven:3.5.3-jdk-8-alpine

# metadata
LABEL module.maintainer="onesaitplatform@indra.es" \
	  module.name="microservice-manager"

# Copy resources on previous layers
WORKDIR /app
COPY ./flask /app/flask

ADD *.jar /app/app.jar
ADD docker_entrypoint.sh /app/docker_entrypoint.sh

# logs folder
RUN mkdir -p /var/log/platform-logs && \
	mkdir ./target
VOLUME ["/tmp", "/var/log/platform-logs"]

# ports
EXPOSE 30010

# envs
#HZ_SERVICE_DISCOVERY_STRATEGY can take values: service or zookeeper
ENV JAVA_OPTS="$JAVA_OPTS -Xms1G -Xmx3G" \
    SERVER_NAME=localhost \
    HZ_SERVICE_DISCOVERY_STRATEGY=service \
    HZ_ZOOKEEPER_URL=zookeeper:2181

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ENV PATH /opt/conda/bin:$PATH

# external install
RUN apt-get update --fix-missing && apt-get install -y apt-utils wget bzip2 ca-certificates \
    libglib2.0-0 libxext6 libsm6 libxrender1 \
    git mercurial subversion

RUN wget --quiet https://repo.anaconda.com/miniconda/Miniconda2-4.5.11-Linux-x86_64.sh -O ~/miniconda.sh && \
    /bin/bash ~/miniconda.sh -b -p /opt/conda && \
    rm ~/miniconda.sh && \
    ln -s /opt/conda/etc/profile.d/conda.sh /etc/profile.d/conda.sh && \
    echo ". /opt/conda/etc/profile.d/conda.sh" >> ~/.bashrc && \
    echo "conda activate base" >> ~/.bashrc

# conda
# Create and activate python environment
RUN conda env create -f ./flask/environment.yml
RUN echo "source activate env" > ~/.bashrc

# Set envs
ENV PATH=/app:/opt/conda/envs/env/bin:$PATH \
    FLASK_ENV=app.py \
    SERVICE_DEBUG_LEVEL=debug

# Uncomment this line to set input properties by default
#ENV ONESAIT_SERVER_NAME=<platform-host> \
#    OAUTH_CLIENT=onesaitplatform \ 
#    PORT=30010 \ 
#    SERVER_NAME=localhost \
#    CONTEXT_PATH=/microservice \
#    OAUTH_SECRET=onesaitplatform

# Start services
ENTRYPOINT ["docker_entrypoint.sh"]
