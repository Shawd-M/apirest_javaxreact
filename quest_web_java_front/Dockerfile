# Étape de build
FROM node:18-alpine AS build

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build

FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 3000

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]