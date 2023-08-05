package com.example.mychat.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.databinding.ItemContainerRecentConversionBinding;
import com.example.mychat.models.ChatMessage;

import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

        private final List<ChatMessage> chatMessages;
        public RecentConversationsAdapter(List<ChatMessage> chatMessages){
                this.chatMessages = chatMessages;
        }

        @NonNull
        @Override
        public RecentConversationsAdapter.ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ConversionViewHolder(
                        ItemContainerRecentConversionBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
        }

        @Override
        public void onBindViewHolder(@NonNull RecentConversationsAdapter.ConversionViewHolder holder, int position) {
                holder.setData(chatMessages.get(position));
        }

        @Override
        public int getItemCount() {
                return chatMessages.size();
        }

        class ConversionViewHolder extends RecyclerView.ViewHolder{
                ItemContainerRecentConversionBinding binding;
                ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding){
                        super(itemContainerRecentConversionBinding.getRoot());
                        binding = itemContainerRecentConversionBinding;
                }
                void setData(ChatMessage chatMessage){
                        binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
                        binding.textName.setText(chatMessage.conversionName);
                        binding.textRecentMessage.setText(chatMessage.message);
                }
        }
        private Bitmap getConversionImage(String encodedImage){
                byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
}
