FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

RUN mkdir -p /var/www/cache/tmp && \
    chown -R nginx:nginx /var/www/cache
