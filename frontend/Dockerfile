FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY . .
RUN npm run build -- --configuration=production
RUN if [ -d /app/dist/beneficio-frontend/browser ]; then cp -r /app/dist/beneficio-frontend/browser/. /app/dist/beneficio-frontend/ && rm -rf /app/dist/beneficio-frontend/browser; fi

FROM nginx:alpine
COPY --from=build /app/dist/beneficio-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
