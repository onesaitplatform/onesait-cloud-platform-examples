FROM continuumio/miniconda3

LABEL maintainer="support@onesaitplatform.com"

# Install packages and tools
RUN apt-get update && apt-get install -y \
  apt-utils \
  wget

# Copy resources on previous layers
WORKDIR /app
COPY . /app

# Create and activate python environment
RUN conda env create -f environment.yml
RUN echo "source activate env" > ~/.bashrc

# Set envs
ENV PATH=/app:/opt/conda/envs/env/bin:$PATH \
    FLASK_ENV=app.py \
    SERVICE_DEBUG_LEVEL=info

# Expose the application's port
EXPOSE 5000

# Start service
CMD ["gunicorn", "-b 0.0.0.0:5000", "-w 1", "app:ModelController", "--log-level="$SERVICE_DEBUG_LEVEL]
